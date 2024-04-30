package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    Collection<ItemDto> getUsersItems(Long userId);

    ItemDto findByid(Long itemId);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    Collection<ItemDto> searchByText(String text);

}