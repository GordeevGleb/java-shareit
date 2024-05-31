package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final RequestMapper requestMapper;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;


    @Override
    public ItemRequestDto create(Long userId, ItemRequestIncDto itemRequestIncDto) {
        log.info("creating request");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user id " + userId + " not found"));
        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestIncDto.getDescription())
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        itemRequestRepository.save(itemRequest);

        ItemRequestDto savedRequest = requestMapper.toItemRequestDto(itemRequest,
                userMapper.toUserDto(user));
        log.info("request id {} created", savedRequest.getId());
        return savedRequest;
    }

    @Override
    public List<ItemRequestDto> get(Long userId) {
        log.info("forming user id {} request list", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user id " + userId + " not found"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);

        if (itemRequests.size() == 0) {
            log.info("user id {} request list formed; size: 0", userId);
            return new ArrayList<>();
        }

        List<Long> requestIdList = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIdIn(requestIdList);
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> requestItems = items.stream()
                    .filter(item -> item.getRequest().getId().equals(itemRequest.getId()))
                    .collect(Collectors.toList());
            ItemRequestDto itemRequestDto = requestMapper.toItemRequestDto(itemRequest, userMapper.toUserDto(user));

            if (!requestItems.isEmpty()) {
                List<ItemDto> itemDtos = requestItems.stream().map(itemMapper::toItemDto)
                        .collect(Collectors.toList());
                itemRequestDto.setItems(itemDtos);
            } else {
                itemRequestDto.setItems(new ArrayList<>());
            }

            itemRequestDtos.add(itemRequestDto);
        }
        log.info("user id {} request list formed; size: {}", userId, itemRequests.size());
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        log.info("forming requests paged list");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user id " + userId + " not found"));
        if (from < 0 || size < 1) {
            throw new PaginationException("wrong pagination params");
        }
        PageRequest pageRequest = PageRequest.of(from, size, Sort.Direction.DESC, "created");
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdIsNot(userId, pageRequest).toList();
        if (requests.size() == 0) {
            log.info("request list formed; size = 0");
            return new ArrayList<>();
        }

        List<Long> requestIds = requests.stream().map(itemRequest -> itemRequest.getId()).collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);
        List<ItemRequestDto> resultList = new ArrayList<>();
        for (ItemRequest itemRequest : requests) {
            List<ItemDto> itemDtos = items.stream()
                    .filter(item -> item.getRequest().getId().equals(itemRequest.getId()))
                    .map(item -> itemMapper.toItemDto(item))
                    .collect(Collectors.toList());
            UserDto requester = userMapper.toUserDto(itemRequest.getRequester());
            ItemRequestDto itemRequestDto = requestMapper.toItemRequestDto(itemRequest, requester);
            itemRequestDto.setItems(itemDtos);
            resultList.add(itemRequestDto);
        }
        log.info("request list formed; size: {}", resultList.size());
        return resultList;
    }

    @Override
    public ItemRequestDto getById(Long userId, Long itemRequestId) {
        log.info("looking for a request id {}", itemRequestId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user id " + userId + " not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("request id " + itemRequestId + " not found"));
        List<ItemDto> items = itemRepository.findAllByRequestId(itemRequestId).stream()
                .map(item -> itemMapper.toItemDto(item))
                .collect(Collectors.toList());
        ItemRequestDto itemRequestDto = requestMapper.toItemRequestDto(itemRequest, userMapper.toUserDto(user));
        itemRequestDto.setItems(items);
        log.info("request id {} found", itemRequestId);
        return itemRequestDto;
    }


}
