package ru.alexadler9.tgnotificationmanagerbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alexadler9.tgnotificationmanagerbot.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
