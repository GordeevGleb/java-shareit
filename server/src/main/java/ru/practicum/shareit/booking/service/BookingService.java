package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.State;

import java.util.List;

public interface BookingService {

    BookingInfoDto create(Long userId, BookingDto bookingDto);

    BookingInfoDto updateStatus(Long userId, Long bookingId, Boolean isApproved);

    BookingInfoDto get(Long userId, Long bookingId);

    List<BookingInfoDto> getUsersBookings(Long userId, State state, Integer from, Integer size);

    List<BookingInfoDto> getOwnersBookings(Long userId, State state, Integer from, Integer size);

    Boolean isExist(Long bookingId);
}
