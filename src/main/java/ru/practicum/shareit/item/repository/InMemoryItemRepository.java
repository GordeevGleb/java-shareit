package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {


    private final HashMap<Long, Item> items = new HashMap<>();

    private Long counter = 1L;

    @Override
    public Item create(Item item) {
        item.setId(counter++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Collection<Item> getUsersItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item findByid(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item update(Long itemId, Item item) {

        Item oldItem = items.get(itemId);

        if (Optional.ofNullable(item.getName()).isPresent()) {
            oldItem.setName(item.getName());
        }
        if (Optional.ofNullable(item.getDescription()).isPresent()) {
            oldItem.setDescription(item.getDescription());
        }
        if (Optional.ofNullable(item.getAvailable()).isPresent()) {
            oldItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, oldItem);
        return oldItem;
    }

    @Override
    public Collection<Item> searchByText(String text) {
        if (!text.isBlank()) {
            return items.values().stream()
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .filter(item -> item.getAvailable().equals(true))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public Boolean isExist(Long id) {
        return items.containsKey(id);
    }
}
