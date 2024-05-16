package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.LastBookingDto;
import ru.practicum.shareit.booking.dto.NextBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "id", source = "booking.id")
    @Mapping(target = "start", source = "booking.start", dateFormat = "YYYY-MM-DDTHH:mm:ss")
    @Mapping(target = "end", source = "booking.end", dateFormat = "YYYY-MM-DDTHH:mm:ss")
    @Mapping(target = "item", source = "itemDto")
    @Mapping(target = "booker", source = "userDto")
    @Mapping(target = "status", source = "booking.bookingStatus")
    BookingInfoDto toBookingInfoDto(Booking booking, UserDto userDto, ItemDto itemDto);

    @Mapping(target = "id", source = "bookingInfoDto.id")
    @Mapping(target = "start", source = "bookingInfoDto.start", dateFormat = "YYYY-MM-DDTHH:mm:ss")
    @Mapping(target = "end", source = "bookingInfoDto.end", dateFormat = "YYYY-MM-DDTHH:mm:ss")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "bookingStatus", source = "bookingInfoDto.status")
    Booking toBooking(User booker, BookingInfoDto bookingInfoDto, Item item);

    @Mapping(target = "id", source = "booking.id")
    @Mapping(target = "bookerId", source = "userId")
    @Mapping(target = "start", source = "booking.start", dateFormat = "YYYY-MM-DDTHH:mm:ss")
    @Mapping(target = "end", source = "booking.end", dateFormat = "YYYY-MM-DDTHH:mm:ss")
    LastBookingDto toLastBookingDto(Booking booking, Long userId);

    @Mapping(target = "id", source = "booking.id")
    @Mapping(target = "bookerId", source = "userId")
    @Mapping(target = "start", source = "booking.start", dateFormat = "YYYY-MM-DDTHH:mm:ss")
    @Mapping(target = "end", source = "booking.end", dateFormat = "YYYY-MM-DDTHH:mm:ss")
    NextBookingDto toNextBookingDto(Booking booking, Long userId);

}
