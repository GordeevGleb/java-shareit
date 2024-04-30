package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;


public interface UserService {

   UserDto create(UserDto userDto);

   Collection<User> getAll();

   User findByid(Long id);

   User update(Long id, UserDto userDto);

   void delete(Long id);

}
