package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemRepository {
    Item save(Item item);

    Item update(Long id, Item item);

    Item findById(Long id);

    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> search(String text);

    List<Item> findAll();

    void delete(Long id);
}
