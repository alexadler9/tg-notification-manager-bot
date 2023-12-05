package ru.alexadler9.tgnotificationmanagerbot.api;

/**
 *  Bot messages.
 */
public class BotMessage {

    public static final String BOT_MESSAGE_START =
            "Это Telegram-бот для создания напоминаний. Для управления ботом используйте меню";

    public static final String BOT_MESSAGE_NOTIFICATIONS_LIST_EMPTY =
            "Список напоминаний пуст";

    public static final String BOT_MESSAGE_NOTIFICATION_ADD_INSTRUCTIONS =
            "Чтобы добавить новое напоминание, введите сообщение в формате:\n" +
                    "_01.01.2023 07:00 погулять с собакой_";

    public static final String BOT_MESSAGE_NOTIFICATION_ADDED =
            "Новое напоминание добавлено";

    public static final String BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT =
            "Некорректный формат напоминания";

    public static final String BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT_TIME_DATE =
            "Некорректный формат даты и/или времени напоминания";
}
