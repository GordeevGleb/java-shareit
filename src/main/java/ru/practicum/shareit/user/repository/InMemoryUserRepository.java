package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private final HashMap<Long, User> users = new HashMap<>();
    private Long counter = 1L;

    @Override
    public UserDto create(UserDto userDto) {
        userDto.setId(counter++);
        User user = UserMapper.toUser(userDto);
        users.put(user.getId(), user);
        return userDto;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> findByid(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User update(Long id, UserDto userDto) {
        if (isUserExists(id)) {
            User oldUser = users.get(id);
            if (userDto.getName() != null) {
                oldUser.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                oldUser.setEmail(userDto.getEmail());
            }
            return oldUser;
        } else {
            throw new NotFoundException("user id " + id + " not found");
        }
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public boolean isUserExists(Long id) {
        return users.containsKey(id);
    }
}
