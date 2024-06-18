package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestIncDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID) Long userId,
                                 @Valid @RequestBody ItemRequestIncDto itemRequestIncDto) {
        log.info("start creating item request");
        return itemRequestClient.create(userId, itemRequestIncDto);
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader(USER_ID) Long userId) {
        log.info("getting requests by user {}", userId);
        return itemRequestClient.get(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> get(@RequestHeader(USER_ID) Long userId,
                                    @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("getting all item requests");
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID) Long userId, @PathVariable Long requestId) {
        log.info("getting request {}", requestId);
        return itemRequestClient.getById(userId, requestId);
    }
}
