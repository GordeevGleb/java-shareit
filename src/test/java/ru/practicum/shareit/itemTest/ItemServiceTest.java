package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class ItemServiceTest {

    @Autowired
    private final ItemServiceImpl itemService;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @MockBean
    private final BookingRepository bookingRepository;

    @MockBean
    private final CommentRepository commentRepository;

    @Test
    void createTest() {
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        ItemDto itemDto = ItemDto.builder()
                .name("item name")
                .description("description")
                .available(true)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();

        when(itemRepository.save(any()))
                .thenReturn(item);

        itemDto = itemService.create(1L, itemDto);

        assertThat(itemDto, is(notNullValue()));
    }

    @Test
    void updateTest() {
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        when(userRepository.existsById(Mockito.any()))
                .thenReturn(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        ItemDto itemDto = ItemDto.builder()
                .name("name updated")
                .description("description updated")
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

        Item itemUpdated = Item.builder()
                .id(1L)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.save(any()))
                .thenReturn(itemUpdated);

        itemDto = itemService.update(1L, 1L, itemDto);
        assertThat(itemDto, is(notNullValue()));
    }

    @Test
    void getAllTest() {
        User owner = User.builder()
                .id(2L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        when(userRepository.existsById(Mockito.any()))
                .thenReturn(true);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(owner));

        when(itemRepository.findAllByOwnerId(any(), any()))
                .thenReturn(Page.empty());

        Collection<ItemDto> itemDtos = itemService.getUsersItems(2L, 0, 11);
        Assertions.assertTrue(itemDtos.isEmpty());

        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepository.findAllByOwnerId(any(), any()))
                .thenReturn(new PageImpl<>(items));

        LocalDateTime created = LocalDateTime.now();

        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(item)
                .author(booker)
                .created(created)
                .build();

        List<Comment> commentList = List.of(comment);

        when(commentRepository.findAllByItemId(1L))
                .thenReturn(commentList);

        Booking lastBooking = Booking.builder()
                .id(1L)
                .start(created.minusMonths(5))
                .end(created.minusMonths(4))
                .item(item)
                .booker(booker)
                .build();

        Booking nextBooking = Booking.builder()
                .id(2L)
                .start(created.plusDays(1L))
                .end(created.plusDays(2L))
                .item(item)
                .booker(booker)
                .build();

        when(bookingRepository.findAllByItemOwnerIdAndBookingStatusIsAndStartBefore(any(), any(), any(), any()))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findAllByItemOwnerIdAndBookingStatusIsAndStartAfter(any(), any(), any(), any()))
                .thenReturn(List.of(nextBooking));

        itemDtos = itemService.getUsersItems(2L, 0, 11);
        assertThat(itemDtos, is(notNullValue()));

        Item item2 = Item.builder()
                .id(2L)
                .name("item 2 name")
                .description("description 2")
                .available(true)
                .owner(owner)
                .build();

        items.add(item2);
        when(commentRepository.findAllByItemId(2L))
                .thenReturn(Collections.emptyList());

        itemDtos = itemService.getUsersItems(2L, 0, 11);
        assertThat(itemDtos, is(notNullValue()));
    }

    @Test
    void findByIdTest() {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@mail.ru")
                .build();

        User owner = User.builder()
                .id(2L)
                .name("user 2")
                .email("user2@mail.ru")
                .build();

        User booker = User.builder()
                .id(3L)
                .name("user 3")
                .email("user3@mail.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        LocalDateTime created = LocalDateTime.now();
        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(item)
                .author(booker)
                .created(created)
                .build();
        List<Comment> commentList = List.of(comment);

        ItemDto itemDto;

        Booking lastBooking = Booking.builder()
                .id(1L)
                .start(created.minusMonths(5))
                .end(created.minusMonths(4))
                .item(item)
                .booker(booker)
                .build();

        Booking nextBooking = Booking.builder()
                .id(2L)
                .start(created.plusDays(1L))
                .end(created.plusDays(2L))
                .item(item)
                .booker(booker)
                .build();

        when(itemRepository.existsById(any()))
                .thenReturn(true);

        when(userRepository.existsById(any()))
                .thenReturn(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(owner));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(commentRepository.findAllByItemId(1L))
                .thenReturn(commentList);

        itemDto = itemService.findById(1L, 1L);
        assertThat(itemDto, is(notNullValue()));

        when(bookingRepository.findAllByItemOwnerIdAndBookingStatusIsAndStartBefore(any(), any(), any(), any()))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findAllByItemOwnerIdAndBookingStatusIsAndStartAfter(any(), any(), any(), any()))
                .thenReturn(List.of(nextBooking));
        itemDto = itemService.findById(1L, 2L);
        assertThat(itemDto, is(notNullValue()));
    }

    @Test
    void notFoundExceptionTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        NotFoundException notFoundException;

        notFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.create(1L, itemDto));
        assertThat(notFoundException.getMessage(), is("user id 1 not found"));

        notFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.update(1L, 1L, itemDto));
        assertThat(notFoundException.getMessage(), is("user not found"));

        notFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.findById(1L, 1L));
        assertThat(notFoundException.getMessage(), is("item not found"));

        notFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getUsersItems(1L, 0, 11));
        assertThat(notFoundException.getMessage(), is("user id 1 not found"));
    }

    @Test
    void paginationExceptionTest() {
        User owner = User.builder()
                .id(2L)
                .name("user")
                .email("user@mail.ru")
                .build();

        when(userRepository.existsById(any()))
                .thenReturn(true);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(owner));

        PaginationException invalidPageParamsException;

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> itemService.getUsersItems(2L, -1, 11));
        assertThat(invalidPageParamsException.getMessage(), is("wrong pagination params"));

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> itemService.getUsersItems(2L, 0, 0));
        assertThat(invalidPageParamsException.getMessage(), is("wrong pagination params"));

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> itemService.searchByText("text", -1, 11));
        assertThat(invalidPageParamsException.getMessage(), is("wrong pagination params"));

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> itemService.searchByText("text", -1, 0));
        assertThat(invalidPageParamsException.getMessage(), is("wrong pagination params"));
    }

    @Test
    void searchByTextTest() {
        User owner = User.builder()
                .id(2L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        when(userRepository.existsById(any()))
                .thenReturn(true);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(owner));

        Collection<ItemDto> itemDtos = itemService.searchByText("", 0, 11);
        Assertions.assertTrue(itemDtos.isEmpty());

        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.searchByText(any(), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        itemDtos = itemService.searchByText("text", 0, 11);
        Assertions.assertTrue(itemDtos.isEmpty());

        List<Item> items = List.of(item);

        when(itemRepository.searchByText(any(), any()))
                .thenReturn(new PageImpl<>(items));
        itemDtos = itemService.searchByText("item", 0, 11);
        assertThat(itemDtos, is(notNullValue()));
    }
}
