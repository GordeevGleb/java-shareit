package ru.practicum.shareit.itemRequestTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {

    @Autowired
    private final ItemRequestServiceImpl itemRequestService;

    @MockBean
    private final ItemRequestRepository itemRequestRepository;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @Test
    void createTestOk() {
        User requester = User.builder()
                .id(2L)
                .name("test name")
                .email("test@mail.ru")
                .build();

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(requester));

        ItemRequestIncDto requestIncDto = ItemRequestIncDto.builder()
                .description("description")
                .build();

        LocalDateTime now = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(requester)
                .created(now)
                .build();

        when(itemRequestRepository.save(any()))
                .thenReturn(request);

        ItemRequestDto itemRequestDtoCreated = itemRequestService.create(2L, requestIncDto);
        assertThat(itemRequestDtoCreated, is(notNullValue()));
        assertEquals(request.getDescription(), requestIncDto.getDescription());
    }

    @Test
    void createTestFailUserThrowsNotFoundException() {
        ItemRequestIncDto requestIncDto = ItemRequestIncDto.builder()
                .description("description")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.create(1L, requestIncDto));
        assertEquals(notFoundException.getMessage(), "user id 1 not found");
    }

    @Test
    void getTestOk() {
        User requester = User.builder()
                .id(2L)
                .name("test name")
                .email("test@mail.ru")
                .build();

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(requester));

        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(2L))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDto> itemRequestDtos = itemRequestService.get(2L);

        assertTrue(itemRequestDtos.isEmpty());

        LocalDateTime requestCreationDate = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(requester)
                .created(requestCreationDate)
                .build();

        List<ItemRequest> itemRequests = List.of(request);

        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(2L))
                .thenReturn(itemRequests);

        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        List<Item> items = Collections.emptyList();

        when(itemRepository.findAllByRequestIdIn(List.of(1L)))
                .thenReturn(items);

        itemRequestDtos = itemRequestService.get(2L);

        assertTrue(itemRequestDtos.get(0).getItems().isEmpty());

        Item item = Item.builder()
                .id(1L)
                .name("test item name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        items = List.of(item);

        when(itemRepository.findAllByRequestIdIn(List.of(1L)))
                .thenReturn(items);

        itemRequestDtos = itemRequestService.get(2L);

        assertThat(itemRequestDtos, is(notNullValue()));
    }

    @Test
    void getTestFailUserThrowsNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.get(1L));
        assertEquals(notFoundException.getMessage(), "user id 1 not found");
    }

    @Test
    void getAllTestOk() {
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        LocalDateTime requestCreationDate = LocalDateTime.now();

        User requester = User.builder()
                .id(2L)
                .name("test name")
                .email("test@mail.ru")
                .build();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(requester)
                .created(requestCreationDate)
                .build();

        List<ItemRequest> itemRequests = new ArrayList<>();

        when(itemRequestRepository.findAllByRequesterIdIsNot(any(), any()))
                .thenReturn(new PageImpl<>(itemRequests));
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAll(1L, 0, 11);
        assertTrue(itemRequestDtos.isEmpty());

        itemRequests = List.of(request);
        when(itemRequestRepository.findAllByRequesterIdIsNot(any(), any()))
                .thenReturn(new PageImpl<>(itemRequests));

        List<Item> items = Collections.emptyList();
        when(itemRepository.findAllByRequestIdIn(List.of(1L)))
                .thenReturn(items);

        itemRequestDtos = itemRequestService.getAll(1L, 0, 11);
        assertTrue(itemRequestDtos.get(0).getItems().isEmpty());

        Item item = Item.builder()
                .id(1L)
                .name("test name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
        items = List.of(item);

        when(itemRepository.findAllByRequestIdIn(List.of(1L)))
                .thenReturn(items);

        itemRequestDtos = itemRequestService.getAll(1L, 0, 11);
        assertThat(itemRequestDtos, is(notNullValue()));
    }

    @Test
    void getAllTestFailUserThrowsNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAll(1L, 0, 11));
        assertEquals(notFoundException.getMessage(), "user id 1 not found");
    }

    @Test
    void getAllTestFailThrowsPaginationException() {
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        PaginationException invalidPageParamsException;

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> itemRequestService.getAll(1L, -1, 11));
        assertThat(invalidPageParamsException.getMessage(), is("wrong pagination params"));

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> itemRequestService.getAll(1L, 0, 0));
        assertThat(invalidPageParamsException.getMessage(), is("wrong pagination params"));
    }

    @Test
    void getByIdTestOk() {
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        LocalDateTime requestCreationDate = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(owner)
                .created(requestCreationDate)
                .build();

        when(itemRequestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));

        List<Item> items = Collections.emptyList();
        when(itemRepository.findAllByRequestId(1L))
                .thenReturn(items);

        ItemRequestDto itemRequestDto = itemRequestService.getById(1L, 1L);
        assertTrue(itemRequestDto.getItems().isEmpty());

        Item item = Item.builder()
                .id(1L)
                .name("test name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
        items = List.of(item);

        when(itemRepository.findAllByRequestId(1L))
                .thenReturn(items);

        itemRequestDto = itemRequestService.getById(1L, 1L);

        assertThat(itemRequestDto, is(notNullValue()));
    }

    @Test
    void getByIdTestFailUserThrowsNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(1L, 1L));
        assertEquals(notFoundException.getMessage(), "user id 1 not found");
    }

    @Test
    void getByIdTestFailItemRequestThrowsNotFoundException() {
        User user = User.builder()
                .id(1L)
                .name("test name")
                .email("test@mail.ru")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(1L, 1L));
        assertEquals(notFoundException.getMessage(), "request id 1 not found");
    }
}

