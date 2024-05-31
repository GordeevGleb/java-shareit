package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    private final CommentService commentService;

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID) Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID) Long userId,
                       @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping
    public Collection<ItemDto> getUsersItems(@RequestHeader(USER_ID) Long userId,
                                             @RequestParam(defaultValue = "0", required = false)
                                             @Min(0) Integer from,
                                             @RequestParam(defaultValue = "10", required = false)
                                                 @Min(1) Integer size) {
        return itemService.getUsersItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable Long itemId,
                            @RequestHeader(value = USER_ID, required = false) Long userId) {
        return itemService.findById(itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchByText(@RequestParam String text,
                                            @RequestParam(defaultValue = "0", required = false)
                                            @Min(0) Integer from,
                                            @RequestParam(defaultValue = "10", required = false)
                                                @Min(1) Integer size) {
        return itemService.searchByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto comment(@RequestHeader(USER_ID) Long userId, @PathVariable Long itemId,
                              @Valid @RequestBody CommentDto commentDto) {
        return commentService.add(userId, itemId, commentDto);
    }
}
