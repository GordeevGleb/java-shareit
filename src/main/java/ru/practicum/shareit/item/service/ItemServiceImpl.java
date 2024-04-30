package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("creating item");
        ItemDto item = itemRepository.create(userId, itemDto);
        log.info("item {} created", item.getId());
        return item;
    }

    @Override
    public Collection<ItemDto> getUsersItems(Long userId) {
        log.info("send user's item list");
        Collection<ItemDto> items = itemRepository.getUsersItems(userId);
        log.info("user's list size: {}", items.size());
        return items;
    }

    @Override
    public ItemDto findByid(Long itemId) {
        log.info("search item {}", itemId);
        ItemDto itemDto = itemRepository.findByid(itemId);
        log.info("item {} found", itemDto.getId());
        return itemDto;
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        log.info("updating item id {}", itemId);
        ItemDto updatedItemDto = itemRepository.update(userId, itemId, itemDto);
        log.info("item {} updated", itemDto.getId());
        return updatedItemDto;
    }

    @Override
    public Collection<ItemDto> searchByText(String text) {
        log.info("search item by text");
        Collection<ItemDto> itemDtos = itemRepository.searchByText(text);
        log.info("found {} items", itemDtos.size());
        return itemDtos;
    }
}
