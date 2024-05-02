package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {

    Item create(Item item);

    Collection<Item> getUsersItems(Long userId);

    Item findByid(Long id);

    Item update(Long itemId, Item item);

    Collection<Item> searchByText(String text);

    Boolean isExist(Long id);
}
