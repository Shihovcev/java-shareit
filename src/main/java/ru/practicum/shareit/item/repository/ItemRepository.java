package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {

    Item save(Item item);

    Collection<Item> findByOwnerId(Long ownerId);

    void deleteByUserIdAndItemId(Long userId, Long itemId);

    Item findById(Long itemId);

    Item update(Item item);

    Collection<Item> search(String text);
}