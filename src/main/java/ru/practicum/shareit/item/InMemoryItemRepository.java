package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1;

    @Override
    public Item save(Item item) {
        item.setId(nextId++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Long id, Item item) {
        Item existing = items.get(id);
        if (existing == null) throw new NoSuchElementException("Item not found");
        item.setId(id);
        item.setOwner(existing.getOwner());
        items.put(id, item);
        return item;
    }

    @Override
    public Item findById(Long id) {
        return items.get(id);
    }

    @Override
    public List<Item> findAllByOwnerId(Long ownerId) {
        return items.values().stream()
            .filter(i -> i.getOwner() != null && i.getOwner().getId().equals(ownerId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        String lower = text.toLowerCase();
        return items.values().stream()
            .filter(i -> Boolean.TRUE.equals(i.getAvailable()))
            .filter(i -> (i.getName() != null && i.getName().toLowerCase().contains(lower)) ||
                         (i.getDescription() != null && i.getDescription().toLowerCase().contains(lower)))
            .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public void delete(Long id) {
        if (!items.containsKey(id)) throw new NoSuchElementException("Item not found");
        items.remove(id);
    }
}
