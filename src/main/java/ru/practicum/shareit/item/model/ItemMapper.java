package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.model.User;

@Component
public class ItemMapper {

    public Item toItem(ItemDto itemDto, User owner) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : true);
        item.setOwner(owner);
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        itemDto.setOwnerId(item.getOwner().getId());
        itemDto.setRequestId(item.getRequestId());
        return itemDto;
    }

    public Item updateItemFromPatchDto(ItemPatchDto itemPatchDto, Item existingItem) {
        if (itemPatchDto.getName().isPresent()) {
            existingItem.setName(itemPatchDto.getName().get());
        }

        if (itemPatchDto.getDescription().isPresent()) {
            existingItem.setDescription(itemPatchDto.getDescription().get());
        }

        if (itemPatchDto.getAvailable().isPresent()) {
            existingItem.setAvailable(itemPatchDto.getAvailable().get());
        }

        return existingItem;
    }

    public ItemResponseDto toResponseDto(Item item) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(item.getId());
        itemResponseDto.setAvailable(item.isAvailable());
        itemResponseDto.setDescription(item.getDescription());
        itemResponseDto.setName(item.getName());
        itemResponseDto.setOwner(item.getOwner().getId());
        return itemResponseDto;
    }
}