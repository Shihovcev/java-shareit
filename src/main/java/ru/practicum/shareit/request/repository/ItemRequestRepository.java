package ru.practicum.shareit.request.repository;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestRepository {

    ItemRequest save(ItemRequest itemRequest);

    ItemRequest update(ItemRequest itemRequest);

    ItemRequest getById(Long requestId);

    Collection<ItemRequest> getAll();

    void deleteRequestBuId(Long requestId);
}
