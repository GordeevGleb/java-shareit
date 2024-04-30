package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConcurrentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        log.info("creating user");
        String userEmail = userDto.getEmail();
        if (!emailExists(userEmail)) {
            UserDto newUser = userRepository.create(userDto);
            log.info("user {} created", newUser.getId());
            return newUser;
        } else {
            throw new ConcurrentException("email already exists");
        }
    }

    @Override
    public Collection<User> getAll() {
        log.info("send all users");
        Collection<User> users = userRepository.getAll();
        log.info("done. users: {}", users.size());
        return users;
    }

    @Override
    public User findByid(Long id) {
        log.info("search user id {}", id);
        User u = userRepository.findByid(id)
                .orElseThrow(() -> new NotFoundException("user id " + id + " not found"));
        log.info("user {} found", id);
        return u;
    }

    @Override
    public User update(Long id, UserDto userDto) {
        log.info("update user id {}", id);
        User oldUser = userRepository.findByid(id)
                .orElseThrow(() -> new NotFoundException("user not found"));
        String oldUserEmail = oldUser.getEmail();
        String newUserEmail = userDto.getEmail();
        if (emailExists(newUserEmail) && !oldUserEmail.equals(newUserEmail)) {
            throw new ConcurrentException("email already exists");
        }
        log.info("user updated");
        User user = userRepository.update(id, userDto);
        return user;
    }

    @Override
    public void delete(Long id) {
        log.info("delete user {}", id);
        userRepository.delete(id);
        log.info("user removed");
    }

    private boolean emailExists(String email) {
        log.info("email check");
        return userRepository.getAll().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}
