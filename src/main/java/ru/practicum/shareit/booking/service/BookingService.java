package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.List;

public interface BookingService {

    BookingInfoDto create(Long userId, BookingDto bookingDto);

    BookingInfoDto updateStatus(Long userId, Long bookingId, Boolean isApproved);

    BookingInfoDto get(Long userId, Long bookingId);

    List<BookingInfoDto> getUsersBookings(Long userId, String state);

    List<BookingInfoDto> getOwnersBookings(Long userId, String state);

    Boolean isExist(Long bookingId);
}
