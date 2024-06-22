package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	private final BookingClient bookingClient;

	private static final String USER_ID = "X-Sharer-User-Id";

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader(USER_ID) Long userId,
											  @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
											  @PositiveOrZero
											  @RequestParam(name = "from", defaultValue = "0") Integer from,
											  @Positive
											  @RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.info("start getting booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, stateParam, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader(USER_ID) Long userId,
										   @RequestBody @Valid BookingDto requestDto) {
		log.info("start creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID) Long userId,
											 @PathVariable Long bookingId) {
		log.info("start getting booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateStatus(@RequestHeader(USER_ID) Long userId,
											   @PathVariable Long bookingId,
											   @RequestParam Boolean approved) {
		log.info("start updating booking status; booking {}, user {}, new status {}", bookingId, userId, approved);
		return bookingClient.updateStatus(userId, bookingId, approved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestHeader(USER_ID) Long userId,
												   @RequestParam(name = "state", defaultValue = "all") String stateParam,
												   @RequestParam(defaultValue = "0", required = false)
												   @PositiveOrZero Integer from,
												   @RequestParam(defaultValue = "10", required = false)
												   @Positive Integer size) {
		log.info("start getting owners booking; user {}, state {}, from {}, size {}", userId, stateParam, from, size);
		return bookingClient.getOwnerBookings(userId, stateParam, from, size);
	}
}
