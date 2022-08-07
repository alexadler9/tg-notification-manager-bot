package ru.alexadler.tgnotificationmanagerbot.model.message;

/**
 *  User message IDs.
 */
public enum UserMessage {
    /** The user's message does not match any of the patterns */
    USER_MESSAGE_UNDEFINED,
    /** The user started the bot */
    USER_MESSAGE_START,
    /** The user requested to create a notification */
    USER_MESSAGE_NOTIFICATION
}
