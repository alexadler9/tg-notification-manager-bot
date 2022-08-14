package ru.alexadler.tgnotificationmanagerbot.model.message;

/**
 * Bot messages.
 */
public class BotMessage {
    private static final String BOT_MESSAGE_COMMANDS =
            "*Доступные команды:*\n" +
                    "/get \\- получить список всех напоминаний;\n" +
                    "/delete \\- удалить все напоминания;\n" +
                    "/delete _ID_ \\- удалить напоминание с идентификатором \\[ID\\]";

    private static final String BOT_MESSAGE_NOTIFICATION_EXAMPLE =
            "Чтобы добавить новое напоминание, введите сообщение в формате: " +
                    "_01\\.01\\.2022 07:00 погулять с собакой_";

    public static final String BOT_MESSAGE_UNDEFINED_COMMAND =
            "Неизвестная команда\n\n" +
                    BOT_MESSAGE_COMMANDS;

    public static final String BOT_MESSAGE_START =
            "Это Telegram\\-бот для создания напоминаний\n\n" +
                    BOT_MESSAGE_NOTIFICATION_EXAMPLE + "\n\n" +
                    BOT_MESSAGE_COMMANDS;

    public static final String BOT_MESSAGE_NOTIFICATIONS_LIST_EMPTY =
            "Список напоминаний пуст";

    public static final String BOT_MESSAGE_NOTIFICATION_CREATED =
            "Новое напоминание создано";

    public static final String BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT =
            "Некорректный формат напоминания\n\n" +
                    BOT_MESSAGE_NOTIFICATION_EXAMPLE;

    public static final String BOT_MESSAGE_NOTIFICATION_WRONG_FORMAT_TIME_DATE =
            "Некорректный формат даты и/или времени напоминания";

    public static final String BOT_MESSAGE_NOTIFICATIONS_DELETED =
            "Напоминания удалены";

    public static final String BOT_MESSAGE_NOTIFICATION_DELETED =
            "Напоминание удалено";
}
