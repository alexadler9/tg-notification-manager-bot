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

    public static final String BOT_MESSAGE_NOTIFICATION_ID_INSTRUCTIONS =
            "Введите идентификатор напоминания (ID)";

    public static final String BOT_MESSAGE_NOTIFICATION_DELETED =
            "Напоминание удалено";

    public static final String BOT_MESSAGE_NOTIFICATIONS_DELETED =
            "Напоминания удалены";

    public static final String BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT =
            "Некорректный формат напоминания";

    public static final String BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT_TIME_DATE =
            "Некорректный формат даты и/или времени напоминания";

    public static final String BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT_ID =
            "Некорректный формат идентификатора напоминания";
}
