package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    User create(User user);

    Collection<User> getAll();

    Optional<User> findByid(Long id);

    User update(Long id, User user);

    void delete(Long id);

    boolean isUserExists(Long id);
}
