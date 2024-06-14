package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemMapperTest {

    private final ItemMapper itemMapper;

    @Test
    void toItemTest() {
        User user = User.builder()
                .id(1L)
                .name("user name")
                .email("user@mail.ru")
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .build();

        Item item = itemMapper.toItem(itemDto, user);
        assertEquals(item.getOwner(), user);
    }
}
