package ru.alexadler9.tgnotificationmanagerbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Bot updates listener.
 */
@Service
public class TgNotificationManagerBotUpdatesListener implements UpdatesListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TgNotificationManagerBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    public TgNotificationManagerBotUpdatesListener(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            LOGGER.info("Processing update: {}", update);
        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
