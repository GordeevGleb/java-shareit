package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingInfoDto {

    private Long id;


    private LocalDateTime start;


    private LocalDateTime end;

    @ToString.Exclude
    @NotNull
    private ItemDto item;

    @ToString.Exclude
    @NotNull
    private UserDto booker;

    private BookingStatus status;

    private State state;
}
