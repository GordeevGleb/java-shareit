package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NextBookingDto {

    private Long id;

    private Long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;
}
