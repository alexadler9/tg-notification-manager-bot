package ru.alexadler9.tgnotificationmanagerbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.alexadler9.tgnotificationmanagerbot.api.BotKeyboard;
import ru.alexadler9.tgnotificationmanagerbot.api.UserMessageID;
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

        if (currMessage.equals(KB_NOTIFICATION_ADD)) {
            return USER_MESSAGE_NOTIFICATION_ADD_REQUEST;
        }

        if (prevMessage.equals(KB_NOTIFICATION_ADD)) {
            return USER_MESSAGE_NOTIFICATION_ADD;
        }

        return USER_MESSAGE_UNDEFINED;
    }

    /**
     * The function parses a task date/time string.
     * @param dateTime task date/time string.
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
}
