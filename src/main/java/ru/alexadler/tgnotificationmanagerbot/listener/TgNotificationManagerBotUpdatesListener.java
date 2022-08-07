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
    private final String USER_MESSAGE_NOTIFICATION_PATTERN =
            "(\\d{2}.\\d{2}.\\d{4}\s\\d{2}:\\d{2})(\s)([\\w\\W]+)";

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

            final String message = update.message().text();
            final Long userId = update.message().from().id();

            switch (parseUserMessage(message)) {
                case USER_MESSAGE_START -> {
                    LOGGER.debug("Processing start user message");
                    sendBotMessage(userId, BotMessage.BOT_MESSAGE_START);
                }

                case USER_MESSAGE_NOTIFICATION -> {
                    LOGGER.debug("Processing notification user message");
                    Matcher matcher = Pattern.compile(USER_MESSAGE_NOTIFICATION_PATTERN).matcher(update.message().text());
                    if (matcher.matches()) {
                        LocalDateTime notificationDateTime = parseUserMessageDateTime(matcher.group(1));
                        String notificationMessage = matcher.group(3);
                        if (notificationDateTime != null) {
                            notificationTaskService.addNotificationTask(userId, notificationDateTime, notificationMessage);
                            sendBotMessage(userId, BotMessage.BOT_MESSAGE_NOTIFICATION_CREATED);
                        } else {
                            LOGGER.warn("Wrong message date/time format");
                            sendBotMessage(userId, BotMessage.BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT_TIME_DATE);
                        }
                    }
                }

                default -> {
                    LOGGER.warn("Wrong message format");
                    sendBotMessage(userId, BotMessage.BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT);
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
        sendMessage.parseMode(ParseMode.Markdown);
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

        if (Pattern.compile(USER_MESSAGE_NOTIFICATION_PATTERN).matcher(message).matches()) {
            return UserMessage.USER_MESSAGE_NOTIFICATION;
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
     * The function every minute checks actual tasks and sends to users reminders about them.
     */
    @Scheduled(cron = "0 0/1 * * * *")
    public void checkActualNotificationTasks() {
        notificationTaskService.getActualNotificationTasks().forEach(notificationTask -> {
            sendBotMessage(notificationTask.getUserId(), "Напоминание: " + notificationTask.getMessage());
            notificationTaskService.deleteNotificationTask(notificationTask);
        });
    }
}
