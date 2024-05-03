package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectUserOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("creating item");
        User user = UserMapper.toUser(userService.findByid(userId));
        if (userService.isExist(userId)) {
            Item item = itemRepository.create(ItemMapper.toItem(itemDto, user));
            ItemDto toItemDto = ItemMapper.toItemDto(item);
            log.info("item {} created", toItemDto.getId());
            return toItemDto;
        } else {
            throw new NotFoundException("user not found");
        }
    }

    @Override
    public Collection<ItemDto> getUsersItems(Long userId) {
        log.info("send user's item list");
        Collection<ItemDto> items = itemRepository.getUsersItems(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("user's list size: {}", items.size());
        return items;
    }

    @Override
    public ItemDto findByid(Long itemId) {
        log.info("search item {}", itemId);
        Item item = itemRepository.findByid(itemId);
        if (isExist(itemId)) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            log.info("item {} found", itemDto.getId());
            return itemDto;
        } else {
            throw new NotFoundException("item not found");
        }
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        log.info("updating item id {}", itemId);
        if (!userService.isExist(userId)) {
            throw new NotFoundException("user not found");
        }

        if (!isExist(itemId)) {
            throw new NotFoundException("item not found");
        }

        Item item = itemRepository.findByid(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new IncorrectUserOperationException("incorrect user operation");
        }
        User user = UserMapper.toUser(userService.findByid(userId));
        Item newItem = ItemMapper.toItem(itemDto, user);
        ItemDto updatedItemDto = ItemMapper.toItemDto(itemRepository.update(itemId, newItem));
        log.info("item {} updated", itemDto.getId());
        return updatedItemDto;
    }

    @Override
    public Collection<ItemDto> searchByText(String text) {
        log.info("search item by text");
        Collection<ItemDto> itemDtos = itemRepository.searchByText(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("found {} items", itemDtos.size());
        return itemDtos;
    }

    public Boolean isExist(Long id) {
        log.info("item exist check");
        return itemRepository.isExist(id);
    }
}
