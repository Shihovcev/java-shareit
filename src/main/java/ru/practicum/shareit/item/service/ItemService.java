package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    ItemResponseDto create(ItemDto itemDto, Long ownerId);

    ItemResponseDto getById(Long itemId, Long userId);

    ItemResponseDto update(ItemPatchDto itemPatchDto, Long itemId, Long ownerId);

    Collection<ItemResponseDto> search(String text, Long userId);

    Collection<ItemResponseDto> getItems(Long ownerId);

    Item findById(Long itemId);

    void deleteItem(Long itemId, Long userId);

    CommentResponseDto save(Long userId, Long itemId, CommentDto commentDto);

    Collection<CommentResponseDto> findAllByItemId(Long itemId);
}