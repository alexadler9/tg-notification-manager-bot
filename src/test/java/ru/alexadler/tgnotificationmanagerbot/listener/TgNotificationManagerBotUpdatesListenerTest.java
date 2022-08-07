package ru.alexadler.tgnotificationmanagerbot.listener;

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
import ru.alexadler.tgnotificationmanagerbot.model.message.UserMessage;
import ru.alexadler.tgnotificationmanagerbot.service.NotificationTaskService;

import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class TgNotificationManagerBotUpdatesListenerTest {
    @Mock
    private NotificationTaskService notificationTaskService;

    @Mock
    private TelegramBot telegramBot;

    @InjectMocks
    private TgNotificationManagerBotUpdatesListener out;

    @Test
    public void shouldReturnStartMessage() {
        Assertions.assertEquals(UserMessage.USER_MESSAGE_START, out.parseUserMessage("/start"));
    }

    @ParameterizedTest
    @MethodSource("provideParamsForCheckInvalidUserMessage")
    public void shouldReturnUndefinedMessage(String message) {
        Assertions.assertEquals(UserMessage.USER_MESSAGE_UNDEFINED, out.parseUserMessage(message));
    }

    @Test
    public void shouldReturnNotificationMessage() {
        Assertions.assertEquals(UserMessage.USER_MESSAGE_NOTIFICATION, out.parseUserMessage("01.01.2022 12:00 сделать домашнее задание"));
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

    private static Stream<Arguments> provideParamsForCheckInvalidUserMessage() {
        return Stream.of(
                Arguments.of(""),                   //User message is empty
                Arguments.of("Invalid message"),    //Invalid format message
                Arguments.of("01.01.2022 12:00"),   //Notification message is empty
                Arguments.of("01.01.2022 12:00 ")   //Notification message is empty
        );
    }

    private static Stream<Arguments> provideParamsForCheckInvalidNotificationDateTime() {
        return Stream.of(
                Arguments.of("00.01.2022 12:00"),   //Invalid day
                Arguments.of("35.01.2022 12:00"),   //Invalid day
                Arguments.of("01.00.2022 12:00"),   //Invalid month
                Arguments.of("01.13.2022 12:00"),   //Invalid month
                Arguments.of("29.02.2022 12:00"),   //Invalid day of the month
                Arguments.of("01.01.2022 25:00"),   //Invalid hours
                Arguments.of("01.01.2022 00:60")    //Invalid minutes
        );
    }

    private static Stream<Arguments> provideParamsForCheckCorrectNotificationDateTime() {
        return Stream.of(
                Arguments.of("01.01.2022 12:00"),
                Arguments.of("29.02.2024 12:00")
        );
    }
}