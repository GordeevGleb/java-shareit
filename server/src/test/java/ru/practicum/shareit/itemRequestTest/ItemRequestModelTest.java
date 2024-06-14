package ru.practicum.shareit.itemRequestTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestModelTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Autowired
    JacksonTester<ItemRequestIncDto> jsonInc;

    @Test
    void ItemRequestDtoTest() throws Exception {
        ItemRequestIncDto itemRequestIncDto = ItemRequestIncDto.builder()
                .description("description")
                .build();
        UserDto user = UserDto.builder()
                .id(1L)
                .name("user name")
                .email("user@mail.ru")
                .build();
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                itemRequestIncDto.getDescription(),
                user,
                LocalDateTime.now(),
                List.of());

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.requester");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

    @Test
    void itemRequestIncDtoTest() throws IOException {
        ItemRequestIncDto itemRequestIncDto = ItemRequestIncDto.builder()
                .description("description")
                .build();
        JsonContent<ItemRequestIncDto> result = jsonInc.write(itemRequestIncDto);
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

}
