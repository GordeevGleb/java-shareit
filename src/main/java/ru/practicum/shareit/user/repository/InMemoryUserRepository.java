package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
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
    public User create(User user) {
        user.setId(counter++);
        users.put(user.getId(), user);
        return user;
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
    public User update(Long id, User user) {
        if (isUserExists(id)) {
            User oldUser = users.get(id);
            if (user.getName() != null) {
                oldUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                oldUser.setEmail(user.getEmail());
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
