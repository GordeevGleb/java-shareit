package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

	private Long itemId;

	private LocalDateTime start;

	private LocalDateTime end;
}
