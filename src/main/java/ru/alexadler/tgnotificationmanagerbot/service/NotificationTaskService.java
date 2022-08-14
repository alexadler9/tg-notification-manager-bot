package ru.alexadler.tgnotificationmanagerbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.alexadler.tgnotificationmanagerbot.model.NotificationTask;
import ru.alexadler.tgnotificationmanagerbot.repository.NotificationTaskRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

/**
 * Service for managing notification tasks.
 */
@Service
public class NotificationTaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationTaskService.class);

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    /**
     * Get all user tasks.
     * @param userId User id.
     * @return collection of user tasks.
     */
    public Collection<NotificationTask> getAllUserNotificationTasks(long userId) {
        Collection<NotificationTask> allUserNotificationTasks = notificationTaskRepository.findAllByUserId(userId);
        LOGGER.debug("All user notification tasks: {}", allUserNotificationTasks);
        return allUserNotificationTasks;
    }

    /**
     * Add a new task to the database.
     * @param notificationTask new task to add.
     * @return added task.
     */
    public NotificationTask addNotificationTask(NotificationTask notificationTask) {
        NotificationTask newNotificationTask = notificationTaskRepository.save(notificationTask);
        LOGGER.debug("New notification task added: {}", newNotificationTask);
        return newNotificationTask;
    }

    /**
     * Add a new task to the database.
     * @param userId user ID.
     * @param dateTime task date/time.
     * @param message task message.
     */
    public NotificationTask addNotificationTask(Long userId, LocalDateTime dateTime, String message) {
        return addNotificationTask(new NotificationTask(null, userId, dateTime, message));
    }

    /**
     * Get tasks whose date/time corresponds to the current date/time up to a minute.
     * @return collection of actual tasks.
     */
    public Collection<NotificationTask> getActualNotificationTasks() {
        final LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Collection<NotificationTask> notificationTasks = notificationTaskRepository.findAllByDateTime(dateTime);
        if (!notificationTasks.isEmpty()) {
            LOGGER.debug("Actual notification tasks: {}", notificationTasks);
        }
        return notificationTasks;
    }

    /**
     * Remove old tasks whose date/time is less than the current date/time up to a minute.
     */
    @Transactional
    public void deleteOldNotificationTasks() {
        final LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        int removedNumber = notificationTaskRepository.deleteAllByDateTimeBefore(dateTime);
        LOGGER.debug("{} old notification tasks removed", removedNumber);
    }

    /**
     * Remove a task from the database.
     * @param notificationTask task to remove.
     */
    public void deleteNotificationTask(NotificationTask notificationTask) {
        notificationTaskRepository.delete(notificationTask);
        LOGGER.debug("Notification task removed: {}", notificationTask);
    }

    /**
     * Remove all user tasks from the database.
     * @param userId User id.
     */
    @Transactional
    public void deleteAllUserNotificationTasks(long userId) {
        int removedNumber = notificationTaskRepository.deleteAllByUserId(userId);
        LOGGER.debug("{} notification tasks removed for user {}", removedNumber, userId);
    }

    /**
     * Remove user task from the database.
     * @param userId User id.
     * @param notificationTaskId Task id.
     */
    @Transactional
    public void deleteUserNotificationTask(long userId, long notificationTaskId) {
        int removedNumber = notificationTaskRepository.deleteAllByUserIdAndId(userId, notificationTaskId);
        LOGGER.debug("{} notification tasks removed for user {}", removedNumber, userId);
    }
}
