package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.Collection;

public interface ItemService {

    ItemResponseDto create(ItemDto itemDto, Long ownerId);

    ItemResponseDto getById(Long itemId, Long userId);

    ItemResponseDto update(ItemPatchDto itemPatchDto, Long itemId, Long ownerId);

    Collection<ItemResponseDto> search(String text, Long userId);

    Collection<ItemResponseDto> getItems(Long ownerId);

    void deleteItem(Long userId, Long itemId);
}
