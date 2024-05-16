package ru.practicum.shareit.item.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "authorName", source = "name")
    @Mapping(target = "created", source = "comment.created", dateFormat = "YYYY-MM-DDTHH:mm:ss")
    CommentDto toCommentDto(Comment comment, String name);

    @Mapping(target = "id", source = "commentDto.id")
    @Mapping(target = "text", source = "commentDto.text")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "author", source = "user")
    @Mapping(target = "created", source = "localDateTime", dateFormat = "YYYY-MM-DDTHH:mm:ss")
    Comment toComment(CommentDto commentDto, Item item, User user, LocalDateTime localDateTime);
}
