package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.IncorrectUserOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {

    private final UserRepository userRepository;

    private final HashMap<Long, Item> items = new HashMap<>();

    private Long counter = 1L;


    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = userRepository.findByid(userId).orElseThrow(() -> new NotFoundException("user not found"));
        itemDto.setId(counter++);
        Item item = ItemMapper.toItem(itemDto, user);
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> getUsersItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findByid(Long itemId) {
        return ItemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("item not found");
        }
        Item item = items.get(itemId);
        if (!userRepository.isUserExists(userId)) {
            throw new NotFoundException("user not found");
        }
        if (!item.getOwner().getId().equals(userId)) {
            throw new IncorrectUserOperationException("incorrect user");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        items.put(itemId, item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> searchByText(String text) {
        if (!text.isBlank()) {
            return items.values().stream()
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .filter(item -> item.getAvailable().equals(true))
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
