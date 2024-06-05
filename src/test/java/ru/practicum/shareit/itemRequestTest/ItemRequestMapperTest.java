package ru.practicum.shareit.itemRequestTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestMapperTest {

    private final RequestMapper itemRequestMapper;

    private final UserMapper userMapper;

    @Test
    void toItemRequestDtoTest() {
        User user = User.builder()
                .id(1L)
                .name("user name")
                .email("user@mail.ru")
                .build();

        UserDto userDto = userMapper.toUserDto(user);
        ItemRequest itemRequest = ItemRequest.builder()
                .description("description")
                .created(LocalDateTime.now())
                .requester(user)
                .build();

        ItemRequestDto itemRequestDto = itemRequestMapper
                .toItemRequestDto(itemRequest, userDto);

        assertThat(itemRequestDto.getRequester().getName(),
                is(itemRequest.getRequester().getName()));
        assertThat(itemRequestDto.getRequester().getEmail(),
                is(itemRequest.getRequester().getEmail()));
    }
}
