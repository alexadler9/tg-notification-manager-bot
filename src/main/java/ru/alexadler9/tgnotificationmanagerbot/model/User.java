package ru.alexadler9.tgnotificationmanagerbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    /**
     * User ID, aka ID chat.
     */
    @Id
    @Getter @Setter private Long id;

    @Getter @Setter private String name;

    public User() {
    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User " + id + " (" + name + ")";
    }
}
