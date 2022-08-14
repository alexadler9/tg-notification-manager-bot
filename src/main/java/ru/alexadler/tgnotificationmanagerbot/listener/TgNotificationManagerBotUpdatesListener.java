package ru.alexadler.tgnotificationmanagerbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.alexadler.tgnotificationmanagerbot.model.NotificationTask;
import ru.alexadler.tgnotificationmanagerbot.model.message.BotMessage;
import ru.alexadler.tgnotificationmanagerbot.model.message.UserMessage;
import ru.alexadler.tgnotificationmanagerbot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bot updates listener.
 */
@Service
public class TgNotificationManagerBotUpdatesListener implements UpdatesListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TgNotificationManagerBotUpdatesListener.class);

    private final TelegramBot telegramBot;
    private final NotificationTaskService notificationTaskService;

    /** Pattern for parsing a user's request to create a notification */
    private final String USER_MESSAGE_NOTIFICATION_CREATE_PATTERN =
            "(\\d{2}.\\d{2}.\\d{4}\s\\d{2}:\\d{2})(\s)([\\w\\W]+)";

    /** Pattern for parsing a user's request to delete the specified notification */
    private final String USER_MESSAGE_NOTIFICATION_DELETE_PATTERN =
            "^(/delete)(\\s)(\\d+)$";

    public TgNotificationManagerBotUpdatesListener(TelegramBot telegramBot,
                                                   NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            LOGGER.info("Processing update: {}", update);
            if (update.message() != null) {
                final String message = update.message().text();
                final Long userId = update.message().from().id();

                switch (parseUserMessage(message)) {
                    case USER_MESSAGE_START -> {
                        LOGGER.debug("Processing start");
                        sendBotMessageMarkdown(userId, BotMessage.BOT_MESSAGE_START);
                    }

                    case USER_MESSAGE_NOTIFICATIONS_GET -> {
                        LOGGER.debug("Processing get notifications");
                        StringBuilder sbMessage = new StringBuilder();
                        notificationTaskService.getAllUserNotificationTasks(userId).forEach(notificationTask -> {
                            sbMessage.append(convertNotificationTaskToBotMessage(notificationTask)).append("\n");
                        });
                        sendBotMessage(userId, sbMessage.isEmpty() ? BotMessage.BOT_MESSAGE_NOTIFICATIONS_LIST_EMPTY : sbMessage.toString());
                    }

                    case USER_MESSAGE_NOTIFICATION_CREATE -> {
                        LOGGER.debug("Processing create notification");
                        Matcher matcher = Pattern.compile(USER_MESSAGE_NOTIFICATION_CREATE_PATTERN).matcher(update.message().text());
                        if (matcher.matches()) {
                            LocalDateTime notificationDateTime = parseUserMessageDateTime(matcher.group(1));
                            String notificationMessage = matcher.group(3);
                            if (notificationDateTime != null) {
                                notificationTaskService.addNotificationTask(userId, notificationDateTime, notificationMessage);
                                sendBotMessageMarkdown(userId, BotMessage.BOT_MESSAGE_NOTIFICATION_CREATED);
                            } else {
                                LOGGER.warn("Wrong message date/time format");
                                sendBotMessageMarkdown(userId, BotMessage.BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT_TIME_DATE);
                            }
                        }
                    }

                    case USER_MESSAGE_NOTIFICATIONS_DELETE -> {
                        LOGGER.debug("Processing delete notifications");
                        notificationTaskService.deleteAllUserNotificationTasks(userId);
                        sendBotMessageMarkdown(userId, BotMessage.BOT_MESSAGE_NOTIFICATIONS_DELETED);
                    }

                    case USER_MESSAGE_NOTIFICATION_DELETE -> {
                        LOGGER.debug("Processing delete notification");
                        Matcher matcher = Pattern.compile(USER_MESSAGE_NOTIFICATION_DELETE_PATTERN).matcher(update.message().text());
                        if (matcher.matches()) {
                            long id = Long.parseLong(matcher.group(3));
                            notificationTaskService.deleteUserNotificationTask(userId, id);
                            sendBotMessageMarkdown(userId, BotMessage.BOT_MESSAGE_NOTIFICATION_DELETED);
                        }
                    }

                    case USER_MESSAGE_UNDEFINED_COMMAND -> {
                        LOGGER.debug("Processing undefined command");
                        sendBotMessageMarkdown(userId, BotMessage.BOT_MESSAGE_UNDEFINED_COMMAND);
                    }

                    default -> {
                        LOGGER.warn("Wrong message format");
                        sendBotMessageMarkdown(userId, BotMessage.BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT);
                    }
                }
            }
        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /**
     * The function sends a message to the user using telegram API.
     * @param userId user id.
     * @param message message to the user.
     */
    private void sendBotMessage(Long userId, String message) {
        SendMessage sendMessage = new SendMessage(userId, message);
        telegramBot.execute(sendMessage);
    }

    /**
     * The function sends a message to the user using telegram API (with parse mode).
     * @param userId user id.
     * @param message message to the user.
     */
    private void sendBotMessageMarkdown(Long userId, String message) {
        SendMessage sendMessage = new SendMessage(userId, message);
        sendMessage.parseMode(ParseMode.MarkdownV2);
        telegramBot.execute(sendMessage);
    }

    /**
     * The function parses a user message string.
     * @param message user message string.
     * @return user message ID (see {@link UserMessage}).
     */
    public UserMessage parseUserMessage(String message) {
        if (message.equals("/start")) {
            return UserMessage.USER_MESSAGE_START;
        }

        if (message.equals("/get")) {
            return UserMessage.USER_MESSAGE_NOTIFICATIONS_GET;
        }

        if (Pattern.compile(USER_MESSAGE_NOTIFICATION_CREATE_PATTERN).matcher(message).matches()) {
            return UserMessage.USER_MESSAGE_NOTIFICATION_CREATE;
        }

        if (message.equals("/delete")) {
            return UserMessage.USER_MESSAGE_NOTIFICATIONS_DELETE;
        }

        if (Pattern.compile(USER_MESSAGE_NOTIFICATION_DELETE_PATTERN).matcher(message).matches())  {
            return UserMessage.USER_MESSAGE_NOTIFICATION_DELETE;
        }

        if (message.startsWith("/"))  {
            return UserMessage.USER_MESSAGE_UNDEFINED_COMMAND;
        }

        return UserMessage.USER_MESSAGE_UNDEFINED;
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

    /**
     * The function converts content of the notification task into a readable message.
     * @param notificationTask notification task.
     * @return Message.
     */
    private String convertNotificationTaskToBotMessage(NotificationTask notificationTask) {
        return "[" + notificationTask.getId() + "] " +
                notificationTask.getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + " " +
                notificationTask.getMessage();
    }

    /**
     * The function every minute checks actual tasks and sends to users reminders about them.
     */
    @Scheduled(cron = "0 0/1 * * * *")
    public void checkActualNotificationTasks() {
        notificationTaskService.getActualNotificationTasks().forEach(notificationTask -> {
            sendBotMessage(notificationTask.getUserId(), "Напоминание: " + notificationTask.getMessage());
            notificationTaskService.deleteNotificationTask(notificationTask);
        });
    }

    /**
     * The function every day deletes old tasks.
     */
    @Scheduled(cron = "@daily")
    public void deleteOldNotificationTasks() {
        notificationTaskService.deleteOldNotificationTasks();
    }
}
