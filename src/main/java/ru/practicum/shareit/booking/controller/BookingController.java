package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USER_ID = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public BookingInfoDto create(@RequestHeader(USER_ID) Long userId,
                                 @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfoDto updateStatus(@RequestHeader(USER_ID) Long userId,
                                       @PathVariable Long bookingId,
                                       @RequestParam Boolean approved) {
        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingInfoDto get(@RequestHeader(USER_ID) Long userId, @PathVariable Long bookingId) {
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingInfoDto> getUsersBookings(@RequestHeader(USER_ID) Long userId,
                                                 @RequestParam(required = false, defaultValue = "ALL") String state,
                                                 @RequestParam(defaultValue = "0", required = false)
                                                     @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10", required = false)
                                                     @Positive Integer size) {
        return bookingService.getUsersBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> getOwnersBookings(@RequestHeader(USER_ID) Long userId,
                                                  @RequestParam(required = false, defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0", required = false)
                                                      @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10", required = false)
                                                      @Positive Integer size) {
        return bookingService.getOwnersBookings(userId, state, from, size);
    }
}
