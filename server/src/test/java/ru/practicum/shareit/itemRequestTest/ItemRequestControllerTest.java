package ru.practicum.shareit.itemRequestTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    MockMvc mvc;

    ItemRequestIncDto itemRequestDtoCreateTest;

    ItemRequestDto itemRequestDtoCreated;

    ItemDto itemDto;

    @BeforeEach
    public void prepare() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("test name")
                .description("test description")
                .available(true)
                .requestId(1L)
                .build();

        UserDto userDto = UserDto.builder()
                .id(2L)
                .name("test user name")
                .email("test@mail.ru")
                .build();

        itemRequestDtoCreateTest = ItemRequestIncDto.builder()
                .description("some test string")
                .build();

        itemRequestDtoCreated = ItemRequestDto.builder()
                .id(1L)
                .description("some test string")
                .requester(userDto)
                .created(LocalDateTime.now())
                .build();
    }

    @AfterEach
    public void clean() {
        itemRequestDtoCreateTest = null;

        itemRequestDtoCreated = null;

        itemDto = null;
    }

    @Test
    public void createTest() throws Exception {
        when(itemRequestService.create(2L, itemRequestDtoCreateTest))
                .thenReturn(itemRequestDtoCreated);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoCreateTest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoCreated.getDescription()), String.class))
                .andExpect(jsonPath("$.requester", is(itemRequestDtoCreated.getRequester()), UserDto.class))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
    }

    @Test
    public void getOwnRequestsTest() throws Exception {
        itemRequestDtoCreated.setItems(List.of(itemDto));

        when(itemRequestService.get(any()))
                .thenReturn(List.of(itemRequestDtoCreated));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoCreated.getDescription()),
                        String.class))
                .andExpect(jsonPath("$[0].requester", is(itemRequestDtoCreated.getRequester()), UserDto.class))
                .andExpect(jsonPath("$[0].created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].items[0].id",
                        is(itemRequestDtoCreated.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name",
                        is(itemRequestDtoCreated.getItems().get(0).getName()), String.class))
                .andExpect(jsonPath("$[0].items[0].description",
                        is(itemRequestDtoCreated.getItems().get(0).getDescription()), String.class))
                .andExpect(jsonPath("$[0].items[0].available",
                        is(itemRequestDtoCreated.getItems().get(0).getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].items[0].requestId",
                        is(itemRequestDtoCreated.getItems().get(0).getRequestId()), Long.class));
    }

    @Test
    public void getAllTest() throws Exception {
        itemRequestDtoCreated.setItems(List.of(itemDto));

        when(itemRequestService.getAll(any(), any(), any()))
                .thenReturn(List.of(itemRequestDtoCreated));


        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 3L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoCreated.getDescription()),
                        String.class))
                .andExpect(jsonPath("$[0].requester", is(itemRequestDtoCreated.getRequester()), UserDto.class))
                .andExpect(jsonPath("$[0].created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].items[0].id",
                        is(itemRequestDtoCreated.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name",
                        is(itemRequestDtoCreated.getItems().get(0).getName()), String.class))
                .andExpect(jsonPath("$[0].items[0].description",
                        is(itemRequestDtoCreated.getItems().get(0).getDescription()), String.class))
                .andExpect(jsonPath("$[0].items[0].available",
                        is(itemRequestDtoCreated.getItems().get(0).getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].items[0].requestId",
                        is(itemRequestDtoCreated.getItems().get(0).getRequestId()), Long.class));
    }

    @Test
    public void getRequestById() throws Exception {
        itemRequestDtoCreated.setItems(List.of(itemDto));

        when(itemRequestService.getById(3L, 1L))
                .thenReturn(itemRequestDtoCreated);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 3L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoCreated.getDescription()), String.class))
                .andExpect(jsonPath("$.requester", is(itemRequestDtoCreated.getRequester()), UserDto.class))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.items[0].id",
                        is(itemRequestDtoCreated.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name",
                        is(itemRequestDtoCreated.getItems().get(0).getName()), String.class))
                .andExpect(jsonPath("$.items[0].description",
                        is(itemRequestDtoCreated.getItems().get(0).getDescription()), String.class))
                .andExpect(jsonPath("$.items[0].available",
                        is(itemRequestDtoCreated.getItems().get(0).getAvailable()), Boolean.class));
    }
}
