package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConcurrentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

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
            UserDto newUser = UserMapper.toUserDto(userRepository.create(UserMapper.toUser(userDto)));
            log.info("user {} created", newUser.getId());
            return newUser;
        } else {
            throw new ConcurrentException("email already exists");
        }
    }

    @Override
    public Collection<UserDto> getAll() {
        log.info("send all users");
        Collection<UserDto> users = userRepository.getAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("done. users: {}", users.size());
        return users;
    }

    @Override
    public UserDto findByid(Long id) {
        log.info("search user id {}", id);
        User user = userRepository.findByid(id)
                .orElseThrow(() -> new NotFoundException("user id " + id + " not found"));
        log.info("user {} found", id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        log.info("update user id {}", id);
        User oldUser = userRepository.findByid(id)
                .orElseThrow(() -> new NotFoundException("user not found"));
        String oldUserEmail = oldUser.getEmail();
        String newUserEmail = userDto.getEmail();
        if (emailExists(newUserEmail) && !oldUserEmail.equals(newUserEmail)) {
            throw new ConcurrentException("email already exists");
        }
        log.info("user updated");
        User user = userRepository.update(id, UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(Long id) {
        log.info("delete user {}", id);
        userRepository.delete(id);
        log.info("user removed");
    }

    private Boolean emailExists(String email) {
        log.info("email check");
        return userRepository.getAll().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public Boolean isExist(Long id) {
        log.info("user exist check");
        return userRepository.isUserExists(id);
    }
}
