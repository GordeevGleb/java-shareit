package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    UserDto create(UserDto userDto);
    Collection<User> getAll();
    Optional<User> findByid(Long id);
    User update(Long id, UserDto userDto);
    void delete(Long id);

    boolean isUserExists(Long id);
}
