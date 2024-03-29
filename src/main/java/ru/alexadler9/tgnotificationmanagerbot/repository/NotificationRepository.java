package ru.alexadler9.tgnotificationmanagerbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.alexadler9.tgnotificationmanagerbot.model.Notification;
import ru.alexadler9.tgnotificationmanagerbot.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Collection<Notification> findAllByDateTime(LocalDateTime dateTime);

    Collection<Notification> findAllByUser(User user);

    @Modifying
    @Query(value = "DELETE FROM notifications WHERE date_time < :dateTime", nativeQuery = true)
    int deleteAllByDateTimeBefore(LocalDateTime dateTime);

    @Modifying
    @Query(value = "DELETE FROM notifications WHERE id_user = :userId", nativeQuery = true)
    int deleteAllByIdUser(Long userId);

    @Modifying
    @Query(value = "DELETE FROM notifications WHERE id_user = :userId AND id = :id", nativeQuery = true)
    int deleteAllByIdUserAndId(Long userId, Long id);
}
