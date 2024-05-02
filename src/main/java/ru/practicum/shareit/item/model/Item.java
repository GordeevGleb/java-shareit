package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder
public class Item {
    private Long id;
    private String name;
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
