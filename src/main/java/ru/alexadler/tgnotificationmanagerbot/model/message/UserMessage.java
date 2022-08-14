package ru.alexadler.tgnotificationmanagerbot.model.message;

/**
 *  User message IDs.
 */
public enum UserMessage {
    /** The user's message does not match any of the patterns */
    USER_MESSAGE_UNDEFINED,
    /** The user sent an unknown command */
    USER_MESSAGE_UNDEFINED_COMMAND,
    /** The user started the bot */
    USER_MESSAGE_START,
    /** The user requested a list of notifications */
    USER_MESSAGE_NOTIFICATIONS_GET,
    /** The user requested to create a notification */
    USER_MESSAGE_NOTIFICATION_CREATE,
    /** The user requested to delete all notifications */
    USER_MESSAGE_NOTIFICATIONS_DELETE,
    /** The user requested to delete the specified notification */
    USER_MESSAGE_NOTIFICATION_DELETE
}
