package ru.alexadler9.tgnotificationmanagerbot.api;

/**
 *  User message IDs.
 */
public enum UserMessageID {
    /** Undefined user message */
    USER_MESSAGE_UNDEFINED,
    /** The user started the bot */
    USER_MESSAGE_START,
    /** The user requested a list of notifications */
    USER_MESSAGE_NOTIFICATIONS_GET_REQUEST,
    /** The user requested to add a notification */
    USER_MESSAGE_NOTIFICATION_ADD_REQUEST,
    /** The user adds a notification */
    USER_MESSAGE_NOTIFICATION_ADD,
    /** The user requested to delete a notification by ID */
    USER_MESSAGE_NOTIFICATION_DELETE_BY_ID_REQUEST,
    /** The user specified a notification ID to delete */
    USER_MESSAGE_NOTIFICATION_DELETE_BY_ID,
    /** The user requested to delete all notifications */
    USER_MESSAGE_NOTIFICATIONS_DELETE_ALL_REQUEST
}
