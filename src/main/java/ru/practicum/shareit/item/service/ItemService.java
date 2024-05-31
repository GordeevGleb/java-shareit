package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    Collection<ItemDto> getUsersItems(Long userId, Integer from, Integer size);

    ItemDto findById(Long itemId, Long userId);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    Collection<ItemDto> searchByText(String text, Integer from, Integer size);
}
