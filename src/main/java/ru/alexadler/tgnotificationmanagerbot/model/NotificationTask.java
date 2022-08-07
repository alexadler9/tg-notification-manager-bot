package ru.alexadler.tgnotificationmanagerbot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * The entity of the "notification_tasks" table.
 */
@Entity
@Table(name = "notification_tasks")
public class NotificationTask {
    @Id
    @Column(columnDefinition = "bigserial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private LocalDateTime dateTime;
    private String message;

    public NotificationTask() {
    }

    public NotificationTask(Long id, Long userId, LocalDateTime dateTime, String message) {
        this.id = id;
        this.userId = userId;
        this.dateTime = dateTime;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask notificationTask = (NotificationTask) o;
        return  (id.equals(notificationTask.getId()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Notification " + id + ". " +
                "[" + userId + ", " + dateTime + "] : " +
                message;
    }
}
