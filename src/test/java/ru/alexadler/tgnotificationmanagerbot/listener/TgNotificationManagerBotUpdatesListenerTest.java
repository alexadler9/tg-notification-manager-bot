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

    @Test
    public void shouldReturnNotificationsGetMessage() {
        Assertions.assertEquals(UserMessage.USER_MESSAGE_NOTIFICATIONS_GET, out.parseUserMessage("/get"));
    }

    @Test
    public void shouldReturnNotificationCreateMessage() {
        Assertions.assertEquals(UserMessage.USER_MESSAGE_NOTIFICATION_CREATE, out.parseUserMessage("01.01.2022 12:00 сделать домашнее задание"));
    }

    @Test
    public void shouldReturnNotificationsDeleteMessage() {
        Assertions.assertEquals(UserMessage.USER_MESSAGE_NOTIFICATIONS_DELETE, out.parseUserMessage("/delete"));
    }

    @Test
    public void shouldReturnNotificationDeleteMessage() {
        Assertions.assertEquals(UserMessage.USER_MESSAGE_NOTIFICATION_DELETE, out.parseUserMessage("/delete 20"));
    }

    @ParameterizedTest
    @MethodSource("provideParamsForCheckUndefinedUserCommand")
    public void shouldReturnUndefinedCommand(String message) {
        Assertions.assertEquals(UserMessage.USER_MESSAGE_UNDEFINED_COMMAND, out.parseUserMessage(message));
    }

    @ParameterizedTest
    @MethodSource("provideParamsForCheckUndefinedUserMessage")
    public void shouldReturnUndefinedMessage(String message) {
        Assertions.assertEquals(UserMessage.USER_MESSAGE_UNDEFINED, out.parseUserMessage(message));
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

    private static Stream<Arguments> provideParamsForCheckUndefinedUserCommand() {
        return Stream.of(
                Arguments.of("/"),                  //Command is empty
                Arguments.of("/add"),               //Unknown command
                Arguments.of("/delete 20 task"),    //Extra parameter in the delete command
                Arguments.of("/delete task 20")     //Extra parameter in the delete command
        );
    }

    private static Stream<Arguments> provideParamsForCheckUndefinedUserMessage() {
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