package ru.practicum.shareit.bookingTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.dto.State.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    @Autowired
    private final BookingServiceImpl bookingService;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @MockBean
    private final BookingRepository bookingRepository;

    @Test
    void createTestOk() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.WAITING)
                .build();

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingInfoDto bookingInfoDto = bookingService.create(3L, bookingDto);
        assertThat(bookingInfoDto, is(notNullValue()));
    }

    @Test
    void createTestFailUserThrowsNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.create(1L, bookingDto));
        assertEquals(notFoundException.getMessage(), "user id 1 not found");
    }

    @Test
    void createTestFailItemThrowsNotFoundException() {
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.create(1L, bookingDto));
        assertEquals(notFoundException.getMessage(), "item not found");
    }

    @Test
    void createTestFailUserEqualsOwnerThrowsNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.create(1L, bookingDto));
        assertEquals(notFoundException.getMessage(), "user id 1 not found");
    }

    @Test
    void createTestFailItemNotAvailableException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(false)
                .owner(owner)
                .build();
        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        NotAvailableException notAvailableException = assertThrows(NotAvailableException.class,
                () -> bookingService.create(3L, bookingDto));
        assertEquals(notAvailableException.getMessage(), "item item name not available");
    }

    @Test
    void createTestFailBookingStartTimeAfterEndThrowsDateTimeException() {
        LocalDateTime start = LocalDateTime.now().plusDays(3L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        DateTimeException dateTimeException = assertThrows(DateTimeException.class,
                () -> bookingService.create(3L, bookingDto));
        assertEquals(dateTimeException.getMessage(), "date time exception");
    }

    @Test
    void createTestFailBookingStartTimeEqualsEndThrowsDateTimeException() {
        LocalDateTime start = LocalDateTime.of(2024, 11, 11, 11, 11, 11);
        LocalDateTime end = LocalDateTime.of(2024, 11, 11, 11, 11, 11);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        DateTimeException dateTimeException = assertThrows(DateTimeException.class,
                () -> bookingService.create(3L, bookingDto));
        assertEquals(dateTimeException.getMessage(), "date time exception");
    }

    @Test
    void updateStatusThenApprovedTestOk() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        BookingInfoDto bookingInfoDto = bookingService.updateStatus(1L, 1L, true);
        assertThat(bookingInfoDto, is(notNullValue()));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(
                        invocation -> {
                            Booking invoc = invocation.getArgument(0, Booking.class);
                            invoc.setBookingStatus(BookingStatus.APPROVED);
                            return invoc;
                        }
                );
        assertThat(bookingInfoDto, is(notNullValue()));
        assertThat(bookingInfoDto.getStatus(), is(BookingStatus.APPROVED));
    }

    @Test
    void updateStatusThenRejectedOk() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        BookingInfoDto bookingInfoDto = bookingService
                .updateStatus(1L, 1L, false);
        assertThat(bookingInfoDto, is(notNullValue()));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(
                        invocation -> {
                            Booking invoc = invocation.getArgument(0, Booking.class);
                            invoc.setBookingStatus(BookingStatus.REJECTED);
                            return invoc;
                        }
                );
        assertThat(bookingInfoDto, is(notNullValue()));
        assertThat(bookingInfoDto.getStatus(), is(BookingStatus.REJECTED));
    }

    @Test
    void updateStatusTestFailUserThrowsNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.updateStatus(1L, 1L, true));
        assertEquals(notFoundException.getMessage(), "user id 1 not found");
    }

    @Test
    void updateStatusTestFailBookingThrowsNotFoundException() {
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.updateStatus(3L, 1L, true));
        assertEquals(notFoundException.getMessage(), "booking not found");
    }

    @Test
    void updateStatusTestFailBookingAlreadyApprovedThrowsStatusException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        StatusException statusException = assertThrows(StatusException.class,
                () -> bookingService.updateStatus(3L, 1L, true));
        assertEquals(statusException.getMessage(), "no changes allowed");
    }

    @Test
    void updateStatusTestFailBookingRejectedThrowsStatusException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.REJECTED)
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        StatusException statusException = assertThrows(StatusException.class,
                () -> bookingService.updateStatus(3L, 1L, true));
        assertEquals(statusException.getMessage(), "no changes allowed");
    }

    @Test
    void updateStatusTestFailUserNotOwnerThrowsNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.WAITING)
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.updateStatus(3L, 1L, true));
        assertEquals(notFoundException.getMessage(), "incorrect user operation");
    }

    @Test
    void getTestOk() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

         Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        BookingInfoDto bookingInfoDto = bookingService.get(1L, 1L);
        assertThat(bookingInfoDto, is(notNullValue()));

        bookingInfoDto = bookingService.get(3L, 1L);
        assertThat(bookingInfoDto, is(notNullValue()));
    }

    @Test
    void getTestFailBookingThrowsNotfoundException() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.get(1L, 1L));
        assertEquals(notFoundException.getMessage(), "booking id 1 not found");
    }

    @Test
    void getTestFailUserNotBookerOrItemOwnerThrowsNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();
        User anotherUser = User.builder()
                .id(4L)
                .name("another user")
                .email("anotheruser@mail.ru")
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(4L))
                .thenReturn(Optional.of(anotherUser));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.get(4L, 1L));
        assertEquals(notFoundException.getMessage(), "user must be booker or item owner");
    }

    @Test
    void getUserAllBookingTestOk() {
        LocalDateTime futureStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(2L);
        LocalDateTime pastStart = LocalDateTime.now().minusDays(3L);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(2L);
        LocalDateTime currentStart = LocalDateTime.now().minusDays(3L);
        LocalDateTime currentEnd = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));
        Booking futureBooking = Booking.builder()
                .id(1L)
                .start(futureStart)
                .end(futureEnd)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();
        Booking pastBooking = Booking.builder()
                .id(1L)
                .start(pastStart)
                .end(pastEnd)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();
        Booking currentBooking = Booking.builder()
                .id(1L)
                .start(currentStart)
                .end(currentEnd)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByBookerId(any(), any()))
                .thenReturn(new PageImpl<>(List.of(futureBooking, pastBooking, currentBooking)));

        List<BookingInfoDto> bookingInfoDtoList = bookingService
                .getUsersBookings(3L, "ALL", 0, 11);
        assertFalse(bookingInfoDtoList.isEmpty());
        assertThat(bookingInfoDtoList.size(), is(3));
    }

    @Test
    void getUserFutureBookingsTestOk() {
        LocalDateTime futureStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));
        Booking futureBooking = Booking.builder()
                .id(1L)
                .start(futureStart)
                .end(futureEnd)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByBookerIdAndStartIsAfter(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(futureBooking)));

        List<BookingInfoDto> bookingInfoDtoList = bookingService
                .getUsersBookings(3L, "FUTURE", 0, 11);
        assertFalse(bookingInfoDtoList.isEmpty());
        assertThat(bookingInfoDtoList.size(), is(1));
    }

    @Test
    void getUserPastBookingsTestOk() {
        LocalDateTime pastStart = LocalDateTime.now().minusDays(3L);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));
        Booking pastBooking = Booking.builder()
                .id(1L)
                .start(pastStart)
                .end(pastEnd)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByBookerIdAndEndIsBefore(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(pastBooking)));

        List<BookingInfoDto> bookingInfoDtoList = bookingService
                .getUsersBookings(3L, "PAST", 0, 11);
        assertFalse(bookingInfoDtoList.isEmpty());
        assertThat(bookingInfoDtoList.size(), is(1));
    }

    @Test
    void getUserCurrentBookingsTestOk() {
        LocalDateTime currentStart = LocalDateTime.now().minusDays(3L);
        LocalDateTime currentEnd = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));
        Booking currentBooking = Booking.builder()
                .id(1L)
                .start(currentStart)
                .end(currentEnd)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(currentBooking)));

        List<BookingInfoDto> bookingInfoDtoList = bookingService
                .getUsersBookings(3L, "CURRENT", 0, 11);
        assertFalse(bookingInfoDtoList.isEmpty());
        assertThat(bookingInfoDtoList.size(), is(1));
    }

    @Test
    void getUserWaitingBookingsTestOk() {
        LocalDateTime start = LocalDateTime.now().minusDays(3L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));
        Booking waitingBooking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findAllByBookerIdAndBookingStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(waitingBooking)));

        List<BookingInfoDto> bookingInfoDtoList = bookingService
                .getUsersBookings(3L, "WAITING", 0, 11);
        assertFalse(bookingInfoDtoList.isEmpty());
        assertThat(bookingInfoDtoList.size(), is(1));
    }

    @Test
    void getUserRejectedBookingsTestOk() {
        LocalDateTime start = LocalDateTime.now().minusDays(3L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));
        Booking rejectedBooking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.REJECTED)
                .build();

        when(bookingRepository.findAllByBookerIdAndBookingStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(rejectedBooking)));

        List<BookingInfoDto> bookingInfoDtoList = bookingService
                .getUsersBookings(3L, "REJECTED", 0, 11);
        assertFalse(bookingInfoDtoList.isEmpty());
        assertThat(bookingInfoDtoList.size(), is(1));
    }

    @Test
    void getUsersBookingsTestFailUserThrowsNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getUsersBookings(1L, "ALL", 0, 11));
        assertEquals(notFoundException.getMessage(), "user id 1 not found");
         notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getUsersBookings(1L, "CURRENT", 0, 11));
        assertEquals(notFoundException.getMessage(), "user id 1 not found");
        notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getUsersBookings(1L, "PAST", 0, 11));
        assertEquals(notFoundException.getMessage(), "user id 1 not found");
        notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getUsersBookings(1L, "FUTURE", 0, 11));
        assertEquals(notFoundException.getMessage(), "user id 1 not found");
        notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getUsersBookings(1L, "WAITING", 0, 11));
        assertEquals(notFoundException.getMessage(), "user id 1 not found");
        notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getUsersBookings(1L, "REJECTED", 0, 11));
        assertEquals(notFoundException.getMessage(), "user id 1 not found");
    }

    @Test
    void getUsersBookingsTestFailPaginationException() {
        User owner = User.builder()
                .id(1L)
                .name("owner name")
                .email("owner@mail.ru")
                .build();
        User booker = User.builder()
                .id(2L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test name")
                .description("description")
                .owner(owner)
                .available(true)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        PaginationException paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getUsersBookings(booker.getId(), "ALL", -1, 11));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getUsersBookings(booker.getId(), "ALL", 0, 0));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getUsersBookings(booker.getId(), "CURRENT", -1, 11));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getUsersBookings(booker.getId(), "CURRENT", 0, 0));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getUsersBookings(booker.getId(), "PAST", -1, 11));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getUsersBookings(booker.getId(), "PAST", 0, 0));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getUsersBookings(booker.getId(), "FUTURE", -1, 11));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getUsersBookings(booker.getId(), "FUTURE", 0, 0));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getUsersBookings(booker.getId(), "WAITING", -1, 11));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getUsersBookings(booker.getId(), "WAITING", 0, 0));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getUsersBookings(booker.getId(), "REJECTED", -1, 11));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getUsersBookings(booker.getId(), "REJECTED", 0, 0));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
    }

    @Test
    void getOwnerAllBookingTestOk() {
        LocalDateTime futureStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(2L);
        LocalDateTime pastStart = LocalDateTime.now().minusDays(3L);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(2L);
        LocalDateTime currentStart = LocalDateTime.now().minusDays(3L);
        LocalDateTime currentEnd = LocalDateTime.now().minusDays(2L);

        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        Booking futureBooking = Booking.builder()
                .id(1L)
                .start(futureStart)
                .end(futureEnd)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();
        Booking pastBooking = Booking.builder()
                .id(1L)
                .start(pastStart)
                .end(pastEnd)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();
        Booking currentBooking = Booking.builder()
                .id(1L)
                .start(currentStart)
                .end(currentEnd)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByItemOwnerId(any(), any()))
                .thenReturn(new PageImpl<>(List.of(futureBooking, pastBooking, currentBooking)));

        List<BookingInfoDto> bookingInfoDtoList = bookingService.
                getOwnersBookings(1L, "ALL", 0, 11);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());
        assertEquals(bookingInfoDtoList.size(), 3);
    }

    @Test
    void getFutureOwnersBookingsTestOk() {
        LocalDateTime futureStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        Booking futureBooking = Booking.builder()
                .id(1L)
                .start(futureStart)
                .end(futureEnd)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(futureBooking)));

        List<BookingInfoDto> bookingInfoDtoList = bookingService.
                getOwnersBookings(1L, "FUTURE", 0, 11);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());
        assertEquals(bookingInfoDtoList.size(), 1);
    }

    @Test
    void getOwnersPastBookingsTestOk() {
        LocalDateTime pastStart = LocalDateTime.now().minusDays(3L);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        Booking pastBooking = Booking.builder()
                .id(1L)
                .start(pastStart)
                .end(pastEnd)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(pastBooking)));

        List<BookingInfoDto> bookingInfoDtoList = bookingService.
                getOwnersBookings(1L, "PAST", 0, 11);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());
        assertEquals(bookingInfoDtoList.size(), 1);
    }

    @Test
    void getOwnersCurrentBookingsTestOk() {
        LocalDateTime currentStart = LocalDateTime.now().minusDays(3L);
        LocalDateTime currentEnd = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        Booking currentBooking = Booking.builder()
                .id(1L)
                .start(currentStart)
                .end(currentEnd)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository
                .findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(currentBooking)));

        List<BookingInfoDto> bookingInfoDtoList = bookingService.
                getOwnersBookings(1L, "CURRENT", 0, 11);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());
        assertEquals(bookingInfoDtoList.size(), 1);
    }

    @Test
    void getOwnersWaitingBookingsTestOk() {
        LocalDateTime start = LocalDateTime.now().minusDays(3L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        Booking waitingBooking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository
                .findAllByItemOwnerIdAndBookingStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(waitingBooking)));

        List<BookingInfoDto> bookingInfoDtoList = bookingService.
                getOwnersBookings(1L, "WAITING", 0, 11);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());
        assertEquals(bookingInfoDtoList.size(), 1);
    }

    @Test
    void getOwnersRejectedBookingsTestOk() {
        LocalDateTime start = LocalDateTime.now().minusDays(3L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        Booking rejectedBooking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository
                .findAllByItemOwnerIdAndBookingStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(rejectedBooking)));

        List<BookingInfoDto> bookingInfoDtoList = bookingService.
                getOwnersBookings(1L, "REJECTED", 0, 11);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());
        assertEquals(bookingInfoDtoList.size(), 1);
    }


    @Test
    void getOwnersBookingsTestFailUserThrowsNotFoundException() {
        when(userRepository.existsById(any()))
                .thenReturn(false);
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getOwnersBookings(1L, "ALL", 0, 11));
        assertEquals(notFoundException.getMessage(), "user not found");
    }

    @Test
    void getOwnersBookingsTestFailThrowsPaginationException() {
        User owner = User.builder()
                .id(1L)
                .name("owner name")
                .email("owner@mail.ru")
                .build();
        User booker = User.builder()
                .id(2L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test name")
                .description("description")
                .owner(owner)
                .available(true)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        PaginationException paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getOwnersBookings(booker.getId(), "ALL", -1, 11));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getOwnersBookings(booker.getId(), "ALL", 0, 0));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getOwnersBookings(booker.getId(), "CURRENT", -1, 11));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getOwnersBookings(booker.getId(), "CURRENT", 0, 0));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getOwnersBookings(booker.getId(), "PAST", -1, 11));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getOwnersBookings(booker.getId(), "PAST", 0, 0));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getOwnersBookings(booker.getId(), "FUTURE", -1, 11));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getOwnersBookings(booker.getId(), "FUTURE", 0, 0));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getOwnersBookings(booker.getId(), "WAITING", -1, 11));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getOwnersBookings(booker.getId(), "WAITING", 0, 0));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getOwnersBookings(booker.getId(), "REJECTED", -1, 11));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
        paginationException = assertThrows(PaginationException.class,
                () -> bookingService.getOwnersBookings(booker.getId(), "REJECTED", 0, 0));
        assertEquals(paginationException.getMessage(), "wrong pagination params");
    }

    @Test
    void isExistTestOk() {
        when(bookingRepository.existsById(anyLong()))
                .thenReturn(true);
        assertTrue(bookingService.isExist(1L));
    }

    @Test
    void isExistTestFail() {
        when(bookingRepository.existsById(anyLong()))
                .thenReturn(false);
        assertFalse(bookingService.isExist(1L));
    }
}
