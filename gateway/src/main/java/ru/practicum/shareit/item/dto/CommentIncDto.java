package ru.practicum.shareit.item.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentIncDto {

    @NotBlank
    private String comment;
}
