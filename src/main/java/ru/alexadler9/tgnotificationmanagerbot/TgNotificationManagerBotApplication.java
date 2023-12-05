package ru.alexadler9.tgnotificationmanagerbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TgNotificationManagerBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TgNotificationManagerBotApplication.class, args);
	}
}
