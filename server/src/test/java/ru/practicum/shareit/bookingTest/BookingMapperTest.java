package ru.practicum.shareit.bookingTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.LastBookingDto;
import ru.practicum.shareit.booking.dto.NextBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingMapperTest {

    private final BookingMapper bookingMapper;

    private final UserMapper userMapper;

    private final ItemMapper itemMapper;


    @Test
    void toBookingInfoDtoTest() {
        User user = User.builder()
                .id(1L)
                .name("user name")
                .email("user@mail.ru")
                .build();
        UserDto userDto = userMapper.toUserDto(user);
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .build();
        ItemDto itemDto = itemMapper.toItemDto(item);
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .bookingStatus(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(14))
                .booker(user)
                .build();

        BookingInfoDto bookingInfoDto =
                bookingMapper
                        .toBookingInfoDto(booking, userDto, itemDto);

        assertThat(bookingInfoDto.getItem().getName(), is(booking.getItem().getName()));
        assertThat(bookingInfoDto.getBooker().getName(), is(booking.getBooker().getName()));
        assertThat(bookingInfoDto.getStatus(), is(booking.getBookingStatus()));
        assertThat(bookingInfoDto.getStart(), is(booking.getStart()));
        assertThat(bookingInfoDto.getEnd(), is(booking.getEnd()));
    }

    @Test
    void toLastBookingDtoTest() {
        User user = User.builder()
                .id(1L)
                .name("user name")
                .email("user@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .bookingStatus(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(14))
                .booker(user)
                .build();

        LastBookingDto lastBookingDto = bookingMapper.toLastBookingDto(booking, user.getId());
        assertThat(lastBookingDto.getBookerId(), is(booking.getBooker().getId()));
        assertThat(lastBookingDto.getStart(), is(booking.getStart()));
        assertThat(lastBookingDto.getEnd(), is(booking.getEnd()));
    }

    @Test
    void toNextBookingDto() {
        User user = User.builder()
                .id(1L)
                .name("user name")
                .email("user@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .bookingStatus(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(14))
                .booker(user)
                .build();

        NextBookingDto nextbookingDto = bookingMapper.toNextBookingDto(booking, user.getId());
        assertThat(nextbookingDto.getBookerId(), is(booking.getBooker().getId()));
        assertThat(nextbookingDto.getStart(), is(booking.getStart()));
        assertThat(nextbookingDto.getEnd(), is(booking.getEnd()));
    }
}
