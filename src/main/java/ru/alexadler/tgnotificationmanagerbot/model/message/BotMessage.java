package ru.alexadler.tgnotificationmanagerbot.model.message;

/**
 *  Bot messages.
 */
public class BotMessage {
    public static final String BOT_MESSAGE_START =
            "Это Telegram-бот для создания напоминаний\n\n" +
                    "Чтобы добавить новое напоминание, введите сообщение в формате: " +
                    "_01.01.2022 07:00 погулять с собакой_";

    public static final String BOT_MESSAGE_NOTIFICATION_CREATED =
            "Новое напоминание создано";

    public static final String BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT =
            "Некорректный формат напоминания\n\n" +
                    "Чтобы добавить новое напоминание, введите сообщение в формате: " +
                    "_01.01.2022 07:00 погулять с собакой_";

    public static final String BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT_TIME_DATE =
            "Некорректный формат даты и/или времени напоминания";
}
