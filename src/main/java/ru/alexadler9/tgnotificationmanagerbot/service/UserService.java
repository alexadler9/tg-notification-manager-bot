package ru.alexadler9.tgnotificationmanagerbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.alexadler9.tgnotificationmanagerbot.model.User;
import ru.alexadler9.tgnotificationmanagerbot.repository.UserRepository;

import java.util.Optional;

/**
 * Service for managing users.
 */
@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns user by the specified ID.
     *
     * @param id user ID (ID chat).
     * @return user with the given id or {@literal Optional#empty()} if none found.
     */
    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Save/update user.
     *
     * @param user user.
     * @return New/updated user.
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
