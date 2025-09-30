package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

@Component
public class ItemMapper {

    public Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setDescription(itemDto.getDescription());
        item.setOwnerId(itemDto.getOwnerId());
        item.setName(itemDto.getName());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : true);
        return item;
    }

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(item.isAvailable());
        itemDto.setId(item.getId());
        itemDto.setOwnerId(item.getOwnerId());
        itemDto.setDescription(item.getDescription());
        itemDto.setName(item.getName());
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
        itemResponseDto.setOwnerId(item.getOwnerId());
        return itemResponseDto;
    }
}
