package ru.practicum.shareit.item.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentIncDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;

    @Override
    public CommentDto comment(Long userId, Long itemId, CommentIncDto commentIncDto) {
        log.info("creating a comment");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user id " + userId + " not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item id " + itemId + " not found"));

        Booking booking = bookingRepository.findTopByItemIdAndBookerIdAndEndIsBeforeAndBookingStatusIs(itemId,
                        userId, LocalDateTime.now(), BookingStatus.APPROVED,
                        Sort.by(Sort.Direction.DESC, "end"))
                .orElseThrow(() -> new NotAvailableException("booking not found"));
        Comment comment = commentRepository.save(commentMapper.toComment(commentIncDto, item, user, LocalDateTime.now()));
        log.info("comment created");
        return commentMapper.toCommentDto(comment, user.getName());
    }
}
