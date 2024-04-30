package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    ItemDto create(Long userId, ItemDto itemDto);

    Collection<ItemDto> getUsersItems(Long userId);

    ItemDto findByid(Long id);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    Collection<ItemDto> searchByText(String text);
}
