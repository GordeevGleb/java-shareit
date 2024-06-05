package ru.practicum.shareit.itemRequestTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findAllByRequesterIdOrderByCreatedDescTest() {
        User user = User.builder()
                .name("user name")
                .email("user@mail.ru")
                .build();
        User owner = User.builder()
                .name("owner")
                .email("owner@mail.ru")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .description("description")
                .created(LocalDateTime.now())
                .build();
        Item item = Item.builder()
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();
        userRepository.save(user);
        userRepository.save(owner);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item);

        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequesterIdOrderByCreatedDesc(user.getId());
        assertThat(itemRequests, is(notNullValue()));
        assertThat(itemRequests.size(), is(1));

        List<ItemRequest> emptyItemRequestList =
                itemRequestRepository
                        .findAllByRequesterIdOrderByCreatedDesc(
                                owner.getId());
        assertThat(emptyItemRequestList.size(), is(0));
    }

    @Test
    void findAllByRequesterIdIsNotTest() {
        User user = User.builder()
                .name("user name")
                .email("user@mail.ru")
                .build();
        User owner = User.builder()
                .name("owner")
                .email("owner@mail.ru")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .description("description")
                .created(LocalDateTime.now())
                .build();
        Item item = Item.builder()
                .name("item name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();
        userRepository.save(user);
        userRepository.save(owner);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item);
        PageRequest pageRequest = PageRequest.of(0, 11, Sort.Direction.ASC, "id");
        List<ItemRequest> itemRequests =
                itemRequestRepository.findAllByRequesterIdIsNot(
                        owner.getId(), pageRequest).toList();
        assertThat(itemRequests.size(), is(1));
    }
}
