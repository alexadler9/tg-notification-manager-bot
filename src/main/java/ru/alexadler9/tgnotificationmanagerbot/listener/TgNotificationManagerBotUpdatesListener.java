package ru.alexadler9.tgnotificationmanagerbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.alexadler9.tgnotificationmanagerbot.api.BotKeyboard;
import ru.alexadler9.tgnotificationmanagerbot.api.UserMessageID;
import ru.alexadler9.tgnotificationmanagerbot.model.Notification;
import ru.alexadler9.tgnotificationmanagerbot.model.User;
import ru.alexadler9.tgnotificationmanagerbot.service.NotificationService;
import ru.alexadler9.tgnotificationmanagerbot.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.alexadler9.tgnotificationmanagerbot.api.BotKeyboard.Button.*;
import static ru.alexadler9.tgnotificationmanagerbot.api.BotMessage.*;
import static ru.alexadler9.tgnotificationmanagerbot.api.UserMessageID.*;

/**
 * Bot updates listener.
 */
@Service
public class TgNotificationManagerBotUpdatesListener implements UpdatesListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TgNotificationManagerBotUpdatesListener.class);

    private final TelegramBot telegramBot;
    private final BotKeyboard telegramKeyboard;
    private final UserService userService;
    private final NotificationService notificationService;

    /** Pattern for parsing a user's request to create a notification */
    private final String USER_MESSAGE_NOTIFICATION_PATTERN =
            "(\\d{2}.\\d{2}.\\d{4}\s\\d{2}:\\d{2})(\s)([\\w\\W]+)";

    public TgNotificationManagerBotUpdatesListener(TelegramBot telegramBot,
                                                   BotKeyboard telegramKeyboard,
                                                   UserService userService,
                                                   NotificationService notificationService) {
        this.telegramBot = telegramBot;
        this.telegramKeyboard = telegramKeyboard;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            LOGGER.info("Processing update: {}", update);

            final Long userId = update.message().from().id();
            final String userName = update.message().from().firstName();
            final User user = userService.getUser(userId)
                    .orElse(userService.updateUser(new User(userId, userName)));
            final String prevUserMessage = (user.getLastMessage() == null) ? "" : user.getLastMessage();
            final String currUserMessage = update.message().text();

            switch (parseUserMessage(currUserMessage, prevUserMessage)) {
                case USER_MESSAGE_START -> {
                    LOGGER.debug("Processing start");
                    sendBotMessage(userId, BOT_MESSAGE_START);
                }

                case USER_MESSAGE_NOTIFICATIONS_GET_REQUEST -> {
                    LOGGER.debug("Processing request to get notifications");
                    StringBuilder sbMessage = new StringBuilder();
                    notificationService.getAllUserNotifications(user).forEach(notification -> {
                        sbMessage.append("• ").append(convertNotificationToBotMessage(notification)).append("\n");
                    });
                    sendBotMessage(userId, sbMessage.isEmpty() ? BOT_MESSAGE_NOTIFICATIONS_LIST_EMPTY : sbMessage.toString());
                }

                case USER_MESSAGE_NOTIFICATION_ADD_REQUEST -> {
                    LOGGER.debug("Processing request to add a notification");
                    sendBotMessage(userId, BOT_MESSAGE_NOTIFICATION_ADD_INSTRUCTIONS);
                }

                case USER_MESSAGE_NOTIFICATION_ADD -> {
                    LOGGER.debug("Processing adding a notification");
                    Matcher matcher = Pattern.compile(USER_MESSAGE_NOTIFICATION_PATTERN).matcher(currUserMessage);
                    if (matcher.matches()) {
                        LocalDateTime notificationDateTime = parseUserMessageDateTime(matcher.group(1));
                        String notificationMessage = matcher.group(3);
                        if (notificationDateTime != null) {
                            notificationService.addNotification(user, notificationDateTime, notificationMessage);
                            sendBotMessage(userId, BOT_MESSAGE_NOTIFICATION_ADDED);
                        } else {
                            LOGGER.warn("Wrong message date/time format");
                            sendBotMessage(userId, BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT_TIME_DATE);
                        }
                    } else {
                        LOGGER.warn("Wrong message format");
                        sendBotMessage(userId, BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT);
                    }
                }

                case USER_MESSAGE_NOTIFICATION_DELETE_BY_ID_REQUEST -> {
                    LOGGER.debug("Processing request to delete a notification by ID");
                    sendBotMessage(userId, BOT_MESSAGE_NOTIFICATION_ID_INSTRUCTIONS);
                }

                case USER_MESSAGE_NOTIFICATION_DELETE_BY_ID -> {
                    LOGGER.debug("Processing notification deletion");
                    if (StringUtils.isNumeric(currUserMessage)) {
                        long id = Long.parseLong(currUserMessage.trim());
                        notificationService.deleteNotification(user, id);
                        sendBotMessage(userId, BOT_MESSAGE_NOTIFICATION_DELETED);
                    } else {
                        LOGGER.warn("Wrong message format");
                        sendBotMessage(userId, BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT_ID);
                    }
                }

                case USER_MESSAGE_NOTIFICATIONS_DELETE_ALL_REQUEST -> {
                    LOGGER.debug("Processing all notifications deletion");
                    notificationService.deleteAllNotifications(user);
                    sendBotMessage(userId, BOT_MESSAGE_NOTIFICATIONS_DELETED);
                }

                default -> {
                    LOGGER.info("Undefined message");
                    // Nothing to do
                }
            }

            user.setLastMessage(currUserMessage);
            userService.updateUser(user);
        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /**
     * The function sends a message to the user using telegram API.
     * @param userId user id.
     * @param message message to the user.
     */
    private void sendBotMessage(Long userId, String message) {
        telegramBot.execute(new SendMessage(userId, message)
                .replyMarkup(telegramKeyboard.getMainKeyboardMarkup())
                .parseMode(ParseMode.Markdown));
    }

    /**
     * The function parses a user message.
     * @param currMessage current user message.
     * @param prevMessage previous user message.
     * @return user message ID (see {@link UserMessageID}).
     */
    public UserMessageID parseUserMessage(@NonNull String currMessage, @NonNull String prevMessage) {
        if (currMessage.equals("/start")) {
            return USER_MESSAGE_START;
        }

        if (currMessage.equals(KB_NOTIFICATIONS_GET)) {
            return USER_MESSAGE_NOTIFICATIONS_GET_REQUEST;
        }

        if (currMessage.equals(KB_NOTIFICATION_ADD)) {
            return USER_MESSAGE_NOTIFICATION_ADD_REQUEST;
        }

        if (currMessage.equals(KB_NOTIFICATION_DELETE_BY_ID)) {
            return USER_MESSAGE_NOTIFICATION_DELETE_BY_ID_REQUEST;
        }

        if (currMessage.equals(KB_NOTIFICATIONS_DELETE_ALL)) {
            return USER_MESSAGE_NOTIFICATIONS_DELETE_ALL_REQUEST;
        }

        if (prevMessage.equals(KB_NOTIFICATION_ADD)) {
            return USER_MESSAGE_NOTIFICATION_ADD;
        }

        if (prevMessage.equals(KB_NOTIFICATION_DELETE_BY_ID)) {
            return USER_MESSAGE_NOTIFICATION_DELETE_BY_ID;
        }

        return USER_MESSAGE_UNDEFINED;
    }

    /**
     * The function parses a notification date/time string.
     * @param dateTime notification date/time string.
     * @return LocalDateTime in case of successful parsing. Otherwise, returns null.
     */
    @Nullable
    public LocalDateTime parseUserMessageDateTime(String dateTime) {
        try {
            return LocalDateTime.parse(
                    dateTime,
                    DateTimeFormatter
                            .ofPattern("dd.MM.uuuu HH:mm")
                            .withResolverStyle(ResolverStyle.STRICT)
            );
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * The function converts content of the notification into a readable message.
     * @param notification notification.
     * @return Message.
     */
    private String convertNotificationToBotMessage(Notification notification) {
        return "\\[ID:" + notification.getId() + "] " +
                notification.getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + " " +
                notification.getMessage();
    }

    /**
     * The function every minute checks actual tasks and sends to users notifications about them.
     */
    @Scheduled(cron = "0 0/1 * * * *")
    public void checkActualNotifications() {
        notificationService.getActualNotifications().forEach(notification -> {
            sendBotMessage(notification.getUser().getId(), "Напоминание: *" + notification.getMessage() + "*");
            notificationService.deleteNotification(notification);
        });
    }

    /**
     * The function every day deletes old notifications.
     */
    @Scheduled(cron = "@daily")
    public void deleteOldNotifications() {
        notificationService.deleteOldNotifications();
    }
}
