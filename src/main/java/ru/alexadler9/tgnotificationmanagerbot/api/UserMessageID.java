package ru.alexadler9.tgnotificationmanagerbot.api;

/**
 *  User message IDs.
 */
public enum UserMessageID {
    /** Undefined user message */
    USER_MESSAGE_UNDEFINED,
    /** The user started the bot */
    USER_MESSAGE_START,
    /** The user requested to add a notification */
    USER_MESSAGE_NOTIFICATION_ADD_REQUEST,
    /** The user adds a notification */
    USER_MESSAGE_NOTIFICATION_ADD
}
