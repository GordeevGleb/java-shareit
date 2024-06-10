package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestIncDto itemRequestIncDto);

    List<ItemRequestDto> get(Long userId);

    List<ItemRequestDto> getAll(Long userId, Integer from, Integer size);

    ItemRequestDto getById(Long userId, Long itemRequestId);
}
