package ru.practicum.shareit.bookingTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

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
    void createTest() {
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
    void updateStatusTest() {
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

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingInfoDto bookingInfoDto = bookingService.updateStatus(1L, 1L, true);
        assertThat(bookingInfoDto, is(notNullValue()));

        booking.setBookingStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(
                        invocation -> {
                            Booking invoc = invocation.getArgument(0, Booking.class);
                            invoc.setBookingStatus(BookingStatus.APPROVED);
                            return invoc;
                        }
                );

        bookingInfoDto = bookingService.updateStatus(1L, 1L, true);
        assertThat(bookingInfoDto, is(notNullValue()));

        booking.setBookingStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(
                        invocation -> {
                            Booking invoc = invocation.getArgument(0, Booking.class);
                            invoc.setBookingStatus(BookingStatus.REJECTED);
                            return invoc;
                        }
                );
        bookingInfoDto = bookingService.updateStatus(1L, 1L, false);
        assertThat(bookingInfoDto, is(notNullValue()));
        Assertions.assertEquals(bookingInfoDto.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void getTest() {
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

        final Booking booking = Booking.builder()
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
    void getUserBookingTest() {
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

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(userRepository.findById(3L))
                .thenReturn(Optional.of(booker));

        Booking booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByBookerId(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking1)));

        List<BookingInfoDto> bookingInfoDtoList = bookingService
                .getUsersBookings(3L, "ALL", 0, 11);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());

        start = LocalDateTime.now().minusDays(2L);
        end = LocalDateTime.now().minusDays(1L);
        Booking booking2 = Booking.builder()
                .id(2L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByBookerIdAndEndIsBefore(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking2)));

        bookingInfoDtoList = bookingService.getUsersBookings(3L, "PAST", 0, 11);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());

        start = LocalDateTime.now().plusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking3 = Booking.builder()
                .id(3L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByBookerIdAndStartIsAfter(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking3)));

        bookingInfoDtoList = bookingService.getUsersBookings(3L, "FUTURE", 0, 11);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());

        start = LocalDateTime.now().minusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking4 = Booking.builder()
                .id(4L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking4)));
        bookingInfoDtoList = bookingService.getUsersBookings(3L, "CURRENT", 0, 11);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());
    }

    @Test
    void getOwnerBookingTest() {
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

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        Booking booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByItemOwnerId(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking1)));

        List<BookingInfoDto> bookingInfoDtoList = bookingService.
                getOwnersBookings(1L, "ALL", 0, 11);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());

        start = LocalDateTime.now().minusDays(2L);
        end = LocalDateTime.now().minusDays(1L);
        Booking booking2 = Booking.builder()
                .id(2L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();
        when(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking2)));
        bookingInfoDtoList = bookingService.getOwnersBookings(1L, "PAST", 0, 11);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());

        start = LocalDateTime.now().plusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking3 = Booking.builder()
                .id(3L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking3)));
        bookingInfoDtoList = bookingService.getOwnersBookings(1L, "FUTURE", 0, 11);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());

        start = LocalDateTime.now().minusDays(1L);
        end = LocalDateTime.now().plusDays(2L);
        Booking booking4 = Booking.builder()
                .id(4L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(BookingStatus.APPROVED)
                .build();
        when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking4)));
        bookingInfoDtoList = bookingService.getOwnersBookings(1L, "CURRENT", 0, 11);
        Assertions.assertFalse(bookingInfoDtoList.isEmpty());
    }
}
