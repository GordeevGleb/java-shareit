package ru.practicum.shareit.userTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService mockUserService;

    @Autowired
    private MockMvc mvc;

    private UserDto incUserDto;

    private UserDto userDto;

    private UserDto incUserDtoUpdate;

    private UserDto userDtoUpdated;

    @BeforeEach
    public void prepare() {
        incUserDto = UserDto.builder()
                .name("test inc name")
                .email("testincmail@yandex.com")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("test Dto name")
                .email("testDtoMail@yandex.com")
                .build();

        incUserDtoUpdate = UserDto.builder()
                .name("updatedName")
                .build();

        userDtoUpdated = UserDto.builder()
                .id(1L)
                .name("userUpdate")
                .email("userTest@email.com")
                .build();
    }

    @AfterEach
    public void clean() {
        incUserDto = null;
        userDto = null;
        incUserDtoUpdate = null;
        userDtoUpdated = null;
    }

    @Test
    public void createTest() throws Exception {
        when(mockUserService.create(incUserDto))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(incUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void getAllTest() throws Exception {
        when(mockUserService.getAll())
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }

    @Test
    public void updateTest() throws Exception {
        when(mockUserService.update(1L, incUserDtoUpdate))
                .thenReturn(userDtoUpdated);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(incUserDtoUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoUpdated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoUpdated.getName())))
                .andExpect(jsonPath("$.email", is(userDtoUpdated.getEmail())));
    }

    @Test
    public void findByIdTest() throws Exception {
        when(mockUserService.findById(1L))
                .thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void deleteTest() throws Exception {
        mvc.perform(delete("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(mockUserService).delete(1L);
    }
}
