package ru.practicum.shareit.item.comment.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;

public interface CommentService {

    CommentDto comment(Long userId, Long itemId, CommentDto commentDto);

}
