package ru.practicum.shareit.itemTest.commentTest;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.comment.service.CommentServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentServiceTest {

    @Autowired
    private CommentServiceImpl commentService;

    @MockBean
    private final CommentRepository commentRepository;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @MockBean
    private final BookingRepository bookingRepository;

    @Test
    void addTestOk() {
        User owner = User.builder()
                .id(2L)
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

        User user = User.builder()
                .id(3L)
                .name("user")
                .email("user@mail.ru")
                .build();

        when(userRepository.findById(3L))
                .thenReturn(Optional.of(user));

        LocalDateTime created = LocalDateTime.now();

        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(item)
                .author(user)
                .created(created)
                .build();
        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto commentDto = CommentDto.builder()
                .text("comment")
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(created.minusMonths(5))
                .end(created.minusMonths(4))
                .item(item)
                .booker(user)
                .bookingStatus(BookingStatus.APPROVED)
                .build();
        when(bookingRepository
                .findTopByItemIdAndBookerIdAndEndIsBeforeAndBookingStatusIs(any(), any(), any(), any(), any())
        ).thenReturn(Optional.of(booking));

        commentDto = commentService.comment(3L, 1L, commentDto);
        assertThat(commentDto, is(notNullValue()));
    }

    @Test
    void addTestFailUserNotFoundException() {
        CommentDto commentDto = CommentDto.builder()
                .text("comment")
                .build();
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> commentService.comment(1L, 1L, commentDto));
        assertEquals(notFoundException.getMessage(), "user id 1 not found");
    }

    @Test
    void addTestFailItemNotFoundException() {
        User user = User.builder()
                .id(3L)
                .name("user")
                .email("user@mail.ru")
                .build();
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.empty());
        CommentDto commentDto = CommentDto.builder()
                .text("comment")
                .build();
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> commentService.comment(user.getId(),
                        1L, commentDto));
        assertEquals(notFoundException.getMessage(), "item id 1 not found");
    }

    @Test
    void addTestFailBookingNotAvailableException() {
        User owner = User.builder()
                .id(2L)
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
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        CommentDto commentDto = CommentDto.builder()
                .text("comment")
                .build();
        when(bookingRepository
                .findTopByItemIdAndBookerIdAndEndIsBeforeAndBookingStatusIs(any(), any(), any(), any(), any()))
                .thenReturn(Optional.empty());
        NotAvailableException notAvailableException = assertThrows(NotAvailableException.class,
                () -> commentService.comment(2L, 1L, commentDto));
        assertEquals(notAvailableException.getMessage(), "booking not found");

    }
}
