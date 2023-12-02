package ru.alexadler9.tgnotificationmanagerbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.alexadler9.tgnotificationmanagerbot.model.Notification;
import ru.alexadler9.tgnotificationmanagerbot.model.User;
import ru.alexadler9.tgnotificationmanagerbot.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Service for managing notifications.
 */
@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Get all user notifications.
     * @param user user.
     * @return collection of user notifications.
     */
    public Collection<Notification> getAllUserNotifications(User user) {
        Collection<Notification> allUserNotifications = notificationRepository.findAllByUser(user);
        LOGGER.debug("All user notifications: {}", allUserNotifications);
        return allUserNotifications;
    }

    /**
     * Add a new notification to the database.
     * @param notification new notification to add.
     * @return added notification.
     */
    public Notification addNotification(Notification notification) {
        Notification newNotification = notificationRepository.save(notification);
        LOGGER.debug("New notification added: {}", newNotification);
        return newNotification;
    }

    /**
     * Add a new notification to the database.
     * @param user user.
     * @param dateTime notification date/time.
     * @param message notification message.
     */
    public Notification addNotification(User user, LocalDateTime dateTime, String message) {
        return addNotification(new Notification(null, user, dateTime, message));
    }
}
