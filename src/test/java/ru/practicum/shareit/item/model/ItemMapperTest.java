package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    private final ItemMapper itemMapper = new ItemMapper();

    @Test
    void toItem_ShouldMapItemDtoToItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(10L);

        User owner = new User();
        owner.setId(1L);

        Item result = itemMapper.toItem(itemDto, owner);

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.isAvailable());
        assertEquals(itemDto.getRequestId(), result.getRequestId());
        assertEquals(owner, result.getOwner());
    }

    @Test
    void toItemDto_ShouldMapItemToItemDto() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setRequestId(10L);
        item.setOwner(owner);

        ItemDto result = itemMapper.toItemDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.isAvailable(), result.getAvailable());
        assertEquals(item.getRequestId(), result.getRequestId());
        assertEquals(owner.getId(), result.getOwnerId());
    }

    @Test
    void updateItemFromPatchDto_ShouldUpdateOnlyProvidedFields() {
        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setName("Old Name");
        existingItem.setDescription("Old Description");
        existingItem.setAvailable(false);

        ItemPatchDto patchDto = new ItemPatchDto();
        patchDto.setName(Optional.of("New Name"));
        patchDto.setAvailable(Optional.of(true));

        Item result = itemMapper.updateItemFromPatchDto(patchDto, existingItem);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("Old Description", result.getDescription());
        assertTrue(result.isAvailable());
    }

    @Test
    void updateItemFromPatchDto_WhenOptionalEmpty_ShouldNotUpdateFields() {
        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setName("Old Name");
        existingItem.setDescription("Old Description");
        existingItem.setAvailable(false);

        ItemPatchDto patchDto = new ItemPatchDto();
        patchDto.setName(Optional.empty());
        patchDto.setDescription(Optional.empty());
        patchDto.setAvailable(Optional.empty());

        Item result = itemMapper.updateItemFromPatchDto(patchDto, existingItem);

        assertNotNull(result);
        assertEquals("Old Name", result.getName());
        assertEquals("Old Description", result.getDescription());
        assertFalse(result.isAvailable());
    }

    @Test
    void toResponseDto_ShouldMapItemToItemResponseDto() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setRequestId(10L);
        item.setOwner(owner);

        ItemResponseDto result = itemMapper.toResponseDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.isAvailable(), result.isAvailable());
        assertEquals(owner.getId(), result.getOwner());
    }
}