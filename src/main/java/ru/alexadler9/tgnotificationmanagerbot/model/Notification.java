package ru.alexadler9.tgnotificationmanagerbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @Column(columnDefinition = "bigserial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user")
    @Getter @Setter private User user;

    @Getter @Setter private LocalDateTime dateTime;

    @Getter @Setter private String message;

    public Notification() {
    }

    public Notification(Long id, User user, LocalDateTime dateTime, String message) {
        this.id = id;
        this.user = user;
        this.dateTime = dateTime;
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification notification = (Notification) o;
        return  (id.equals(notification.getId()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + ". " +
                "[" + user.getId() + ", " + dateTime + "] : " +
                message;
    }
}
