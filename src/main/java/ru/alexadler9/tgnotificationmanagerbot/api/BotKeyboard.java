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
                    new String[] { Button.KB_NOTIFICATIONS_GET }
                )
                .oneTimeKeyboard(false)
                .resizeKeyboard(true)
                .selective(true);
    }
}
