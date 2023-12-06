package ru.alexadler9.tgnotificationmanagerbot.api;

import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import org.springframework.stereotype.Component;

@Component
public class BotKeyboard {

    /**
     *  Keyboard buttons.
     */
    public static class Button {

        public static final String KB_NOTIFICATION_ADD =
                "ДОБАВИТЬ НАПОМИНАНИЕ";

        public static final String KB_NOTIFICATIONS_GET =
                "СПИСОК НАПОМИНАНИЙ";

        public static final String KB_NOTIFICATION_DELETE_BY_ID =
                "УДАЛИТЬ ПО ID";

        public static final String KB_NOTIFICATIONS_DELETE_ALL =
                "УДАЛИТЬ ВСЕ";
    }

    /**
     * Creating a main keyboard.
     *
     * @return created keyboard object.
     */
    public Keyboard getMainKeyboardMarkup() {
        return new ReplyKeyboardMarkup
                (
                    new String[] { Button.KB_NOTIFICATION_ADD },
                    new String[] { Button.KB_NOTIFICATIONS_GET },
                    new String[] { Button.KB_NOTIFICATION_DELETE_BY_ID, Button.KB_NOTIFICATIONS_DELETE_ALL }
                )
                .oneTimeKeyboard(false)
                .resizeKeyboard(true)
                .selective(true);
    }
}
