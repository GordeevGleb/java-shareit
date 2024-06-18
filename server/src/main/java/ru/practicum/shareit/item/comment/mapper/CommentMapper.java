package ru.practicum.shareit.item.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "authorName", source = "name")
    @Mapping(target = "created", source = "comment.created", dateFormat = "YYYY-MM-DDTHH:mm:ss")
    CommentDto toCommentDto(Comment comment, String name);
}
