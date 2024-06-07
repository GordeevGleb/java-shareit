package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final UserMapper userMapper;

    private final ItemMapper itemMapper;



    @Override
    public BookingInfoDto create(Long userId, BookingDto bookingDto) {
        log.info("creating booking");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user id " + userId + " not found"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("item not found"));

        User owner = item.getOwner();

        if (owner.getId().equals(userId)) {
            throw new NotFoundException("user id " + userId + " not found");
        }
        if (!item.getAvailable()) {
            throw new NotAvailableException("item " + item.getName() + " not available");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new DateTimeException("date time exception");
        }

        Booking booking = Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(user)
                .bookingStatus(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);

        BookingInfoDto bookingInfoDto = bookingMapper
                .toBookingInfoDto(booking, userMapper.toUserDto(user), itemMapper.toItemDto(item));
        bookingInfoDto.setState(State.WAITING);
        log.info("booking id {} created", booking.getId());
        return bookingInfoDto;
    }

    @Override
    public BookingInfoDto updateStatus(Long userId, Long bookingId, Boolean isApproved) {
        log.info("update booking status");
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user id " + userId + " not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking not found"));
        log.info("booking id {} found", bookingId);

        if (booking.getBookingStatus().equals(BookingStatus.APPROVED)
                || booking.getBookingStatus().equals(BookingStatus.REJECTED)) {
            throw new StatusException("no changes allowed");
        }
        Item item = booking.getItem();

        if (!item.getOwner().getId().equals(owner.getId())) {
            throw new NotFoundException("incorrect user operation");
        }
        if (isApproved) {
            booking.setBookingStatus(BookingStatus.APPROVED);
            BookingInfoDto bookingInfoDto =
                    bookingMapper.toBookingInfoDto(booking, userMapper.toUserDto(booking.getBooker()),
                            itemMapper.toItemDto(item));
            bookingRepository.save(booking);
            log.info("booking status updated");
            return bookingInfoDto;
        } else {
            booking.setBookingStatus(BookingStatus.REJECTED);
            BookingInfoDto bookingInfoDto =
                    bookingMapper.toBookingInfoDto(booking, userMapper.toUserDto(booking.getBooker()),
                            itemMapper.toItemDto(item));
            bookingInfoDto.setState(State.REJECTED);
            bookingRepository.save(booking);
            log.info("booking status updated");
            return bookingInfoDto;
        }
    }

    @Override
    public BookingInfoDto get(Long userId, Long bookingId) {
        log.info("looking for info about booking id {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking id " + bookingId + " not found"));
        Item item = booking.getItem();
        User owner = item.getOwner();
        User booker = booking.getBooker();
        if (!booker.getId().equals(userId) && !owner.getId().equals(userId)) {
            throw new NotFoundException("user must be booker or item owner");
        }
        BookingInfoDto bookingInfoDto = bookingMapper.toBookingInfoDto(booking, userMapper.toUserDto(booker),
                itemMapper.toItemDto(item));
        log.info("booking found successfully");
            return bookingInfoDto;
    }

    @Override
    public List<BookingInfoDto> getUsersBookings(Long userId,
                                                 String value,
                                                 Integer from,
                                                 Integer size) throws StateException {
        log.info("search user's bookings by state {}", value);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("user id " + userId + " not found");
        }
        if (Arrays.stream(State.values()).noneMatch(state -> state.name().equals(value))) {
            throw new StateException("Unknown state: " + value);
        }
        if (from < 0 || size < 1) {
            throw new PaginationException("wrong pagination params");
        }

        PageRequest pageRequest = PageRequest.of(from / size,
                size, Sort.Direction.DESC, "start");


        State  validatedState = State.valueOf(value);
            Page<Booking> resultList;
            switch (validatedState) {
                case FUTURE:
                    log.info("getting future user's bookings");
                    resultList = bookingRepository.findAllByBookerIdAndStartIsAfter(userId,
                            LocalDateTime.now(),
                            pageRequest);
                    break;
                case PAST:
                    log.info("getting past user's bookings");
                    resultList = bookingRepository.findAllByBookerIdAndEndIsBefore(userId,
                            LocalDateTime.now(),
                            pageRequest);
                    break;
                case CURRENT:
                    log.info("getting current user's bookings");
                    resultList = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId,
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            pageRequest);
                    break;
                case WAITING:
                    log.info("getting user's bookings waiting to approve");
                    resultList = bookingRepository.findAllByBookerIdAndBookingStatus(userId,
                            BookingStatus.WAITING, pageRequest);
                    break;
                case REJECTED:
                    log.info("getting rejected user's bookings");
                    resultList = bookingRepository.findAllByBookerIdAndBookingStatus(userId,
                            BookingStatus.REJECTED, pageRequest);
                    break;
                default:
                    log.info("getting all user's bookings");
                    resultList = bookingRepository.findAllByBookerId(userId, pageRequest);
            }
List<BookingInfoDto> bookingInfoDtoList = resultList.stream().map(booking ->
                bookingMapper.toBookingInfoDto(booking, userMapper.toUserDto(booking.getBooker()),
                        itemMapper.toItemDto(booking.getItem())))
        .collect(Collectors.toList());
        log.info("result list size: {}", bookingInfoDtoList.size());
        return bookingInfoDtoList;
    }

    @Override
    public List<BookingInfoDto> getOwnersBookings(Long userId, String value, Integer from, Integer size) {
        log.info("searching for item owner's bookings");
        if (Arrays.stream(State.values()).noneMatch(state -> state.name().equals(value))) {
            throw new StateException("Unknown state: " + value);
        }
        State validatedState = State.valueOf(value);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("user not found");
        }
        if (from < 0 || size < 1) {
            throw new PaginationException("wrong pagination params");
        }
        PageRequest pageRequest = PageRequest.of(from / size,
                size, Sort.Direction.DESC, "start");
        Page<Booking> resultList;
        switch (validatedState) {
            case FUTURE:
                log.info("getting all item owner's future bookings");
                resultList = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId,
                        LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                log.info("getting all item owner's past bookings");
                resultList = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId,
                        LocalDateTime.now(), pageRequest);
                break;
            case CURRENT:
                log.info("getting current item owner's bookings");
                resultList = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                log.info("getting item owner's bookings with waiting status");
                resultList = bookingRepository.findAllByItemOwnerIdAndBookingStatus(userId,
                        BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                log.info("getting rejected item owner's bookings");
                resultList = bookingRepository.findAllByItemOwnerIdAndBookingStatus(userId,
                        BookingStatus.REJECTED, pageRequest);
                break;
            default:
                log.info("getting all item owner's bookings");
                resultList = bookingRepository.findAllByItemOwnerId(userId, pageRequest);
        }
        List<BookingInfoDto> bookingInfoDtoList = resultList.stream().map(booking ->
                bookingMapper.toBookingInfoDto(booking, userMapper.toUserDto(booking.getBooker()),
                        itemMapper.toItemDto(booking.getItem()))).collect(Collectors.toList());
        log.info("result list size: {}", bookingInfoDtoList.size());
        return bookingInfoDtoList;
    }

    @Override
    public Boolean isExist(Long bookingId) {
        log.info("booking exist check");
        return bookingRepository.existsById(bookingId);
    }
}