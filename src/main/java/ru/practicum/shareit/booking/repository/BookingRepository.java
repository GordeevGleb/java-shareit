package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                                         LocalDateTime start,
                                                                         LocalDateTime end,
                                                                         Sort sort);

    List<Booking> findAllByBookerIdAndBookingStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId,
                                                                            LocalDateTime start,
                                                                            LocalDateTime end,
                                                                            Sort sort);

    List<Booking> findAllByItemOwnerIdAndBookingStatus(Long ownerId, BookingStatus status, Sort sort);

    Optional<Booking> findTopByItemIdAndBookerIdAndEndIsBeforeAndBookingStatusIs(Long itemId,
                                                                          Long bookerId,
                                                                          LocalDateTime endTime,
                                                                          BookingStatus bookingStatus,
                                                                                 Sort sort);

    List<Booking> findAllByItemOwnerIdAndBookingStatusIsAndStartBefore(Long userId,
                                                                       BookingStatus bookingStatus,
                                                                       LocalDateTime localDateTime,
                                                                       Sort sort);

    List<Booking> findAllByItemOwnerIdAndBookingStatusIsAndStartAfter(Long userId,
                                                                      BookingStatus bookingStatus,
                                                                      LocalDateTime localDateTime,
                                                                      Sort sort);


}
