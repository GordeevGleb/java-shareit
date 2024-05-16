package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectUserOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingMapper bookingMapper;

    private final ItemMapper itemMapper;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("creating item");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user id " + userId + " not found"));
        Item item = itemRepository.save(itemMapper.toItem(itemDto, user));
        ItemDto toItemDto = itemMapper.toItemDto(item);
        log.info("item {} created", toItemDto.getId());
        return toItemDto;
    }

    @Override
    public Collection<ItemDto> getUsersItems(Long userId) {
        log.info("send user's item list");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("user id " + userId + " not found");
        }
        Collection<Item> items = itemRepository.findAllByOwnerId(userId);
        Collection<Booking> lastBookings = bookingRepository
                .findAllByItemOwnerIdAndBookingStatusIsAndStartBefore(userId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now(),
                        Sort.by("start").descending());
        Collection<Booking> nextBookings = bookingRepository
                .findAllByItemOwnerIdAndBookingStatusIsAndStartAfter(userId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now(),
                        Sort.by("start").ascending());
        Collection<Comment> comments = commentRepository.findAllByItemOwnerId(userId);

        Collection<ItemDto> resultList = new ArrayList<>();
        for (Item item : items) {
            List<CommentDto> itemComments = comments.stream()
                    .filter(comment -> comment.getItem().getId().equals(item.getId()))
                    .map(comment -> commentMapper.toCommentDto(comment, comment.getAuthor().getName()))
                    .collect(Collectors.toList());

            Optional<Booking> lastBooking = lastBookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .findFirst();

            Optional<Booking> nextBooking = nextBookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .findFirst();

            ItemDto itemDto = itemMapper.toItemDto(item);
            itemDto.setComments(itemComments);

            lastBooking.ifPresent(booking -> itemDto.setLastBooking(bookingMapper
                    .toLastBookingDto(booking, booking.getBooker().getId())));

            nextBooking.ifPresent(booking -> itemDto.setNextBooking(bookingMapper
                    .toNextBookingDto(booking, booking.getBooker().getId())));

            resultList.add(itemDto);
        }
        log.info("user's list size: {}", items.size());
        return resultList;
    }

    @Override
    public ItemDto findById(Long itemId, Long userId) {
        log.info("search item {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item not found"));
        if (Optional.ofNullable(userId).isPresent() && (item.getOwner().getId().equals(userId))) {
            Collection<Booking> lastBookings = bookingRepository
                    .findAllByItemOwnerIdAndBookingStatusIsAndStartBefore(userId,
                            BookingStatus.APPROVED,
                            LocalDateTime.now(),
                            Sort.by("start").descending());
            Collection<Booking> nextBookings = bookingRepository
                    .findAllByItemOwnerIdAndBookingStatusIsAndStartAfter(userId,
                            BookingStatus.APPROVED,
                            LocalDateTime.now(),
                            Sort.by("start").ascending());


            ItemDto itemDto = itemMapper.toItemDto(item);

            Optional<Booking> lastBooking = lastBookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .findFirst();

            Optional<Booking> nextBooking = nextBookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .findFirst();

            lastBooking.ifPresent(booking -> itemDto.setLastBooking(bookingMapper
                    .toLastBookingDto(booking, booking.getBooker().getId())));

            nextBooking.ifPresent(booking -> itemDto.setNextBooking(bookingMapper
                    .toNextBookingDto(booking, booking.getBooker().getId())));

            List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());

            itemDto.setComments(comments.stream().map(comment ->
                    commentMapper.toCommentDto(comment, comment.getAuthor().getName())).collect(Collectors.toList()));

            log.info("item {} found", itemDto.getId());
            return itemDto;
        }
        ItemDto itemDto = itemMapper.toItemDto(item);
        List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
        itemDto.setComments(comments.stream().map(comment ->
                commentMapper.toCommentDto(comment, comment.getAuthor().getName())).collect(Collectors.toList()));

        log.info("item {} found", itemDto.getId());
        return itemDto;
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        log.info("updating item id {}", itemId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("user not found");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new IncorrectUserOperationException("incorrect user operation");
        }


        if (Optional.ofNullable(itemDto.getName()).isPresent()) {
            item.setName(itemDto.getName());
        }

        if (Optional.ofNullable(itemDto.getDescription()).isPresent()) {
            item.setDescription(itemDto.getDescription());
        }

        if (Optional.ofNullable(itemDto.getAvailable()).isPresent()) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);
        ItemDto updatedItemDto = itemMapper.toItemDto(item);
        log.info("item {} updated", itemDto.getId());
        return updatedItemDto;
    }

    @Override
    public Collection<ItemDto> searchByText(String text) {
        log.info("search item by text");
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        Collection<ItemDto> itemDtos = itemRepository
                .searchByText(text)
                .stream()
                .map(item -> itemMapper.toItemDto(item))
                .collect(Collectors.toList());
        log.info("found {} items", itemDtos.size());
        return itemDtos;
    }
}
