package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private static final Set<Item> items = new HashSet<>();
    private final AtomicLong generateIds = new AtomicLong(0);

    @Override
    public Item save(Item item) {
        log.info("Сохраняю вещь : {}", item.getName());
        item.setId(generateIds.incrementAndGet());
        items.add(item);
        return item;
    }

    @Override
    public Collection<Item> findByOwnerId(Long ownerId) {
        log.info("Ищу вещи владельца : {}", ownerId);
        return items.stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByUserIdAndItemId(Long userId, Long itemId) {
        log.info("Удаляю вещь : {}, {}", itemId, userId);
        items.removeIf(item -> item.getOwnerId().equals(userId) && item.getId().equals(itemId));
    }

    @Override
    public Item findById(Long itemId) {
        log.info("Ищу по Ид вещь : {}", itemId);
        return items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));
    }

    @Override
    public Item update(Item item) {
        log.info("Обновляю вещь : {}", item.getName());
        Item item1 = findById(item.getId());
        if (item.getOwnerId() != null) {
            item1.setOwnerId(item.getOwnerId());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            item1.setDescription(item.getDescription());
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            item1.setName(item.getName());
        }
        item1.setAvailable(item.isAvailable());

        return item1;
    }

    @Override
    public Collection<Item> search(String text) {
        log.info("Ищу вещь по имени и описанию : {}", text);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase();

        return items.stream()
                .filter(Item::isAvailable)
                .filter(item -> (item.getName() != null && item.getName().toLowerCase().contains(searchText)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(searchText))
                )
                .collect(Collectors.toList());
    }

}