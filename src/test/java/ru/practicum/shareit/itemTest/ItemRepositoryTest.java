package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void searchByTextTest() {
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        Item item1 = Item.builder()
                .name("item 1 name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();


        PageRequest pageRequest = PageRequest.of(0, 11, Sort.Direction.ASC, "id");
        userRepository.save(owner);
        Item savedItem1 = itemRepository.save(item1);

        List<Item> itemList = itemRepository.searchByText("1", pageRequest).toList();

        assertNotNull(itemList);
        assertEquals(itemList.size(), 1);
        assertEquals(item1.getName(), savedItem1.getName());
        assertEquals(item1.getDescription(), savedItem1.getDescription());

        List<Item> anotherItemList = itemRepository.searchByText("some another text", pageRequest).toList();
        assertEquals(anotherItemList.size(), 0);

        userRepository.deleteAll();
        itemRepository.deleteAll();
    }
}
