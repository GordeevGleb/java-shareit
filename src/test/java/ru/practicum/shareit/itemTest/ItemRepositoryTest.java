package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRepositoryTest {

@Autowired
    private UserRepository userRepository;

@Autowired
    private ItemRepository itemRepository;

@Autowired
private ItemRequestRepository itemRequestRepository;

    private User owner;

    private Item item1;

    private Item item2;

    private PageRequest pageRequest;

    private ItemRequest itemRequest;

    private ItemRequest anotherItemRequest;


    @Test
    void findAllByOwnerIdTest() {
        owner = User.builder()
                .name("owner")
                .email("owner@mail.ru")
                .build();

        item1 = Item.builder()
                .name("item 1 name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        item2 = Item.builder()
                .name("item 2 name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        pageRequest = PageRequest.of(0, 11, Sort.Direction.ASC, "id");
        owner = userRepository.save(owner);

        Item savedItem1 = itemRepository.save(item1);
        Item savedItem2 = itemRepository.save(item2);

        List<Item> itemList = itemRepository.findAllByOwnerId(owner.getId(), pageRequest).toList();
        assertNotNull(itemList);
        assertEquals(itemList.size(), 2);
        assertEquals(savedItem1.getName(), item1.getName());
        assertEquals(savedItem1.getDescription(), item2.getDescription());
        assertEquals(savedItem2.getName(), item2.getName());
        assertEquals(savedItem2.getDescription(), item2.getDescription());
    }

    @Test
    void searchByTextTest() {
        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        item1 = Item.builder()
                .name("item 1 name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();


        pageRequest = PageRequest.of(0, 11, Sort.Direction.ASC, "id");
        owner = userRepository.save(owner);
        Item savedItem1 = itemRepository.save(item1);

        List<Item> itemList = itemRepository.searchByText("1", pageRequest).toList();

        assertNotNull(itemList);
        assertEquals(itemList.size(), 1);
        assertEquals(item1.getName(), savedItem1.getName());
        assertEquals(item1.getDescription(), savedItem1.getDescription());

List<Item> anotherItemList = itemRepository.searchByText("some another text", pageRequest).toList();
assertEquals(anotherItemList.size(), 0);
    }

    @Test
    void findAllByRequestId() {
        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .description("1")
                .requester(owner)
                .created(LocalDateTime.now())
                .build();

        pageRequest = PageRequest.of(0, 11, Sort.Direction.ASC, "id");
        owner = userRepository.save(owner);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);

        item1 = Item.builder()
                .name("item 1 name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        item2 = Item.builder()
                .name("item 2 name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        Item savedItem1 = itemRepository.save(item1);
        Item savedItem2 = itemRepository.save(item2);

        List<Item> itemList = itemRepository.findAllByRequestId(savedItemRequest.getId());

        assertNotNull(itemList);
        assertEquals(itemList.size(), 2);
        assertEquals(item1.getName(), savedItem1.getName());
        assertEquals(item1.getDescription(), savedItem1.getDescription());
        assertEquals(item2.getName(), savedItem2.getName());
        assertEquals(item2.getDescription(), savedItem2.getDescription());


         anotherItemRequest = ItemRequest.builder()
                .description("2")
                .requester(owner)
                .created(LocalDateTime.now())
                .build();

        List<Item> anotherItemList = itemRepository.findAllByRequestId(anotherItemRequest.getId());
        assertEquals(anotherItemList.size(), 0);
    }

    @Test
    void findAllByRequestIdInTest() {
        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .description("1")
                .requester(owner)
                .created(LocalDateTime.now())
                .build();

        anotherItemRequest = itemRequest = ItemRequest.builder()
                .description("2")
                .requester(owner)
                .created(LocalDateTime.now())
                .build();

        pageRequest = PageRequest.of(0, 11, Sort.Direction.ASC, "id");
        owner = userRepository.save(owner);
        ItemRequest savedItemRequest1 = itemRequestRepository.save(itemRequest);
        ItemRequest savedItemRequest2 = itemRequestRepository.save(anotherItemRequest);

        item1 = Item.builder()
                .name("item 1 name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(savedItemRequest1)
                .build();

        item2 = Item.builder()
                .name("item 2 name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(savedItemRequest2)
                .build();

        Item savedItem1 = itemRepository.save(item1);
        Item savedItem2 = itemRepository.save(item2);

        List<Long> itemRequestIdList = itemRequestRepository.findAll()
                .stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> itemList = itemRepository.findAllByRequestIdIn(itemRequestIdList);

        assertNotNull(itemList);
        assertEquals(itemList.size(), 2);
        assertEquals(item1.getName(), savedItem1.getName());
        assertEquals(item1.getDescription(), savedItem1.getDescription());
        assertEquals(item2.getName(), savedItem2.getName());
        assertEquals(item2.getDescription(), savedItem2.getDescription());
    }
}
