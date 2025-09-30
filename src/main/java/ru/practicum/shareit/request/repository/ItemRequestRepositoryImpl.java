package ru.practicum.shareit.request.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ItemRequestRepositoryImpl implements ItemRequestRepository {
    private static final Set<ItemRequest> requests = new HashSet<>();
    private final AtomicLong generateIds = new AtomicLong(0);

    @Override
    public ItemRequest save(ItemRequest itemRequest) {
        itemRequest.setId(generateIds.incrementAndGet());
        requests.add(itemRequest);
        return itemRequest;
    }

    @Override
    public ItemRequest update(ItemRequest itemRequest) {
        return itemRequest;
    }

    @Override
    public ItemRequest getById(Long requestId) {
        return requests.stream()
                .filter(itemRequest -> itemRequest.getId().equals(requestId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
    }

    @Override
    public Collection<ItemRequest> getAll() {
        return new ArrayList<>();
    }

    @Override
    public void deleteRequestBuId(Long requestId) {

    }
}
