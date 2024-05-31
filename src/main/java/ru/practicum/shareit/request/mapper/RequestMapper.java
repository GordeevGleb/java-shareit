package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "id", source = "itemRequest.id")
    @Mapping(target = "description", source = "itemRequest.description")
    @Mapping(target = "requester", source = "userDto")
    @Mapping(target = "created", source = "itemRequest.created", dateFormat = "YYYY-MM-DDTHH:mm:ss")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest, UserDto userDto);

    @Mapping(target = "id", source = "itemRequestDto.id")
    @Mapping(target = "description", source = "itemRequestDto.description")
    @Mapping(target = "requester", source = "user")
    @Mapping(target = "created", source = "itemRequestDto.created")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user);

}
