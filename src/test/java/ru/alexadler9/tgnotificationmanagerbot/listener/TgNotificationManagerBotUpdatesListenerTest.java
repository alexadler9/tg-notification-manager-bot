package ru.alexadler9.tgnotificationmanagerbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.alexadler9.tgnotificationmanagerbot.api.BotKeyboard;
import ru.alexadler9.tgnotificationmanagerbot.service.NotificationService;
import ru.alexadler9.tgnotificationmanagerbot.service.UserService;

import java.util.stream.Stream;

import static ru.alexadler9.tgnotificationmanagerbot.api.BotKeyboard.Button.*;
import static ru.alexadler9.tgnotificationmanagerbot.api.UserMessageID.*;

@ExtendWith(MockitoExtension.class)
class TgNotificationManagerBotUpdatesListenerTest {

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private BotKeyboard telegramKeyboard;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TgNotificationManagerBotUpdatesListener out;

    @Test
    public void shouldReturnStartMessage() {
        Assertions.assertEquals(USER_MESSAGE_START, out.parseUserMessage("/start", ""));
    }

    @Test
    public void shouldReturnNotificationAddRequestMessage() {
        Assertions.assertEquals(USER_MESSAGE_NOTIFICATION_ADD_REQUEST, out.parseUserMessage(KB_NOTIFICATION_ADD, ""));
    }

    @Test
    public void shouldReturnNotificationAddMessage() {
        Assertions.assertEquals(USER_MESSAGE_NOTIFICATION_ADD, out.parseUserMessage("01.01.2024 12:00 сделать домашнее задание", KB_NOTIFICATION_ADD));
    }

    @ParameterizedTest
    @MethodSource("provideParamsForCheckUndefinedUserMessage")
    public void shouldReturnUndefinedMessage(String currMessage, String prevMessage) {
        Assertions.assertEquals(USER_MESSAGE_UNDEFINED, out.parseUserMessage(currMessage, prevMessage));
    }

    @ParameterizedTest
    @MethodSource("provideParamsForCheckInvalidNotificationDateTime")
    public void shouldReturnNullWhenNotificationDateTimeIsInvalid(String dateTime) {
        Assertions.assertNull(out.parseUserMessageDateTime(dateTime));
    }

    @ParameterizedTest
    @MethodSource("provideParamsForCheckCorrectNotificationDateTime")
    public void shouldReturnDateTimeWhenNotificationDateTimeIsCorrect(String dateTime) {
        Assertions.assertNotNull(out.parseUserMessageDateTime(dateTime));
    }

    private static Stream<Arguments> provideParamsForCheckUndefinedUserMessage() {
        return Stream.of(
                Arguments.of("", ""),                                           // User message is empty
                Arguments.of("Invalid message", ""),                            // Invalid format message
                Arguments.of("01.01.2024 12:00 сделать домашнее задание", "")   // Notification message without a request
        );
    }

    private static Stream<Arguments> provideParamsForCheckInvalidNotificationDateTime() {
        return Stream.of(
                Arguments.of("00.01.2024 12:00"),   // Invalid day
                Arguments.of("35.01.2024 12:00"),   // Invalid day
                Arguments.of("01.00.2024 12:00"),   // Invalid month
                Arguments.of("01.13.2024 12:00"),   // Invalid month
                Arguments.of("29.02.2025 12:00"),   // Invalid day of the month
                Arguments.of("01.01.2024 25:00"),   // Invalid hours
                Arguments.of("01.01.2024 00:60")    // Invalid minutes
        );
    }

    private static Stream<Arguments> provideParamsForCheckCorrectNotificationDateTime() {
        return Stream.of(
                Arguments.of("01.01.2023 12:00"),
                Arguments.of("29.02.2024 18:00"),
                Arguments.of("28.02.2025 00:00")
        );
    }
}