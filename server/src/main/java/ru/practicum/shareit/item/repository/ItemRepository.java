package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerId(Long userId, PageRequest pageRequest);

    @Query("from Item as it " +
            "where " +
            "it.available = true " +
            "and " +
            "(upper(it.name) like upper(concat('%',?1,'%')) or upper(it.description) like upper(concat('%',?1,'%')))")
    Page<Item> searchByText(String text, PageRequest pageRequest);

    List<Item> findAllByRequestIdIn(List<Long> itemRequests);

    List<Item> findAllByRequestId(Long requestId);
}
