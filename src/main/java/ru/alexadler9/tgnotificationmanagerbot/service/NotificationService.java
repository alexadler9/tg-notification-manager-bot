package ru.alexadler9.tgnotificationmanagerbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alexadler9.tgnotificationmanagerbot.model.Notification;
import ru.alexadler9.tgnotificationmanagerbot.model.User;
import ru.alexadler9.tgnotificationmanagerbot.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    /**
     * Get notifications whose date/time corresponds to the current date/time up to a minute.
     * @return collection of actual notifications.
     */
    public Collection<Notification> getActualNotifications() {
        final LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Collection<Notification> notifications = notificationRepository.findAllByDateTime(dateTime);
        if (!notifications.isEmpty()) {
            LOGGER.debug("Actual notifications: {}", notifications);
        }
        return notifications;
    }

    /**
     * Remove old notifications whose date/time is less than the current date/time up to a minute.
     */
    @Transactional
    public void deleteOldNotifications() {
        final LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        int removedNumber = notificationRepository.deleteAllByDateTimeBefore(dateTime);
        LOGGER.debug("{} old notifications removed", removedNumber);
    }

    /**
     * Remove a notification from the database.
     * @param notification notification to remove.
     */
    public void deleteNotification(Notification notification) {
        notificationRepository.delete(notification);
        LOGGER.debug("Notification removed: {}", notification);
    }

    /**
     * Remove a notification from the database.
     * @param user user.
     * @param notificationId notification ID.
     */
    @Transactional
    public void deleteNotification(User user, long notificationId) {
        int removedNumber = notificationRepository.deleteAllByIdUserAndId(user.getId(), notificationId);
        LOGGER.debug("{} notifications removed for user {}", removedNumber, user.getId());
    }

    /**
     * Remove all notifications (for user) from the database.
     * @param user user.
     */
    @Transactional
    public void deleteAllNotifications(User user) {
        int removedNumber = notificationRepository.deleteAllByIdUser(user.getId());
        LOGGER.debug("{} notifications removed for user {}", removedNumber, user.getId());
    }
}
