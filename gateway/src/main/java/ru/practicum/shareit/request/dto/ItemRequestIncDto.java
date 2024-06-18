package ru.practicum.shareit.request.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestIncDto {

    @NotBlank
    private String description;
}
