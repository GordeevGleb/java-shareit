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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        log.info("creating user");
        User user = userMapper.toUser(userDto);
        userRepository.save(user);
        UserDto newUser = userMapper.toUserDto(user);
        log.info("user {} created", newUser.getId());
        return newUser;
    }

    @Override
    public Collection<UserDto> getAll() {
        log.info("send all users");
        Collection<UserDto> users = userRepository.findAll()
                .stream()
                .map(user -> userMapper.toUserDto(user))
                .collect(Collectors.toList());
        log.info("done. users: {}", users.size());
        return users;
    }

    @Override
    public UserDto findById(Long id) {
        log.info("search user id {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user id " + id + " not found"));
        log.info("user {} found", id);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        log.info("update user id {}", id);
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user not found"));
        String oldUserEmail = oldUser.getEmail();
        String newUserEmail = userDto.getEmail();
        if (userRepository.existsByEmail(newUserEmail) && !oldUserEmail.equals(newUserEmail)) {
            throw new ConcurrentException("email already exists");
        }
        if (Optional.ofNullable(userDto.getName()).isPresent()) {
            oldUser.setName(userDto.getName());
        }
        if (Optional.ofNullable(userDto.getEmail()).isPresent()) {
            oldUser.setEmail(userDto.getEmail());
        }
        log.info("user updated");
        User user = userRepository.save(oldUser);
        return userMapper.toUserDto(user);
    }

    @Override
    public void delete(Long id) {
        log.info("delete user {}", id);
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user id " + id + " not found"));
        userRepository.delete(userToDelete);
        log.info("user removed");
    }

    @Override
    public Boolean isExist(Long id) {
        log.info("user exist check");
        return userRepository.existsById(id);
    }
}
