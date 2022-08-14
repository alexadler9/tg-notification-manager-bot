package ru.alexadler.tgnotificationmanagerbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.alexadler.tgnotificationmanagerbot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    Collection<NotificationTask> findAllByDateTime(LocalDateTime dateTime);

    Collection<NotificationTask> findAllByUserId(Long UserId);

    @Modifying
    @Query("DELETE FROM NotificationTask WHERE dateTime < :dateTime")
    int deleteAllByDateTimeBefore(LocalDateTime dateTime);

    @Modifying
    @Query("DELETE FROM NotificationTask WHERE userId = :userId")
    int deleteAllByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM NotificationTask WHERE userId = :userId AND id = :id")
    int deleteAllByUserIdAndId(Long userId, Long id);
}
