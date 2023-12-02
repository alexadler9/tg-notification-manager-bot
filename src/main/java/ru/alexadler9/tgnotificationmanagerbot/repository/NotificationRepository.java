package ru.alexadler9.tgnotificationmanagerbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alexadler9.tgnotificationmanagerbot.model.Notification;
import ru.alexadler9.tgnotificationmanagerbot.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Collection<Notification> findAllByDateTime(LocalDateTime dateTime);

    Collection<Notification> findAllByUser(User user);
}
