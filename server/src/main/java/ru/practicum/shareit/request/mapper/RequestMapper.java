package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "id", source = "itemRequest.id")
    @Mapping(target = "description", source = "itemRequest.description")
    @Mapping(target = "requester", source = "userDto")
    @Mapping(target = "created", source = "itemRequest.created", dateFormat = "YYYY-MM-DDTHH:mm:ss")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest, UserDto userDto);


    @Mapping(target = "description", source = "itemRequestIncDto.description")
    @Mapping(target = "requester", source = "user")
    @Mapping(target = "created", source = "localDateTime")
    ItemRequest toItemRequest(ItemRequestIncDto itemRequestIncDto, User user, LocalDateTime localDateTime);

}
