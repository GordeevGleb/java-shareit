package ru.practicum.shareit.booking.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerId(Long bookerId, PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                                 LocalDateTime start,
                                                                 LocalDateTime end,
                                                                 PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndBookingStatus(Long bookerId, BookingStatus status, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerId(Long ownerId, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime start, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId,
                                                                    LocalDateTime start,
                                                                    LocalDateTime end,
                                                                    PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndBookingStatus(Long ownerId, BookingStatus status, PageRequest pageRequest);

    Optional<Booking> findTopByItemIdAndBookerIdAndEndIsBeforeAndBookingStatusIs(Long itemId,
                                                                                 Long bookerId,
                                                                                 LocalDateTime endTime,
                                                                                 BookingStatus bookingStatus,
                                                                                 Sort sort);

    List<Booking> findAllByItemOwnerIdAndBookingStatusIsAndEndBefore(Long userId,
                                                                       BookingStatus bookingStatus,
                                                                       LocalDateTime localDateTime,
                                                                       Sort sort);

    List<Booking> findAllByItemOwnerIdAndBookingStatusIsAndEndAfter(Long userId,
                                                                      BookingStatus bookingStatus,
                                                                      LocalDateTime localDateTime,
                                                                      Sort sort);


}
