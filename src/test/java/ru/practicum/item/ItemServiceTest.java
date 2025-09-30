package ru.practicum.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void create_ShouldCreateItemAndReturnResponseDto() {
        Long ownerId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwnerId(ownerId);

        ItemResponseDto expectedResponse = new ItemResponseDto();
        expectedResponse.setId(1L);
        expectedResponse.setName("Test Item");
        expectedResponse.setDescription("Test Description");
        expectedResponse.setAvailable(true);
        expectedResponse.setOwnerId(ownerId);

        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setId(ownerId);
        userResponse.setName("User Name");
        userResponse.setEmail("user@example.com");

        when(userService.getById(ownerId)).thenReturn(userResponse);
        when(itemMapper.toItem(itemDto)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toResponseDto(item)).thenReturn(expectedResponse);

        ItemResponseDto result = itemService.create(itemDto, ownerId);

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        assertEquals(expectedResponse.getName(), result.getName());
        assertEquals(expectedResponse.getDescription(), result.getDescription());
        assertEquals(expectedResponse.isAvailable(), result.isAvailable());
        assertEquals(expectedResponse.getOwnerId(), result.getOwnerId());

        verify(userService).getById(ownerId);
        verify(itemMapper).toItem(itemDto);
        verify(itemRepository).save(item);
        verify(itemMapper).toResponseDto(item);
    }

    @Test
    void getById_WhenItemExists_ShouldReturnItem() {
        Long itemId = 1L;
        Long userId = 1L;

        Item item = new Item();
        item.setId(itemId);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwnerId(userId);

        ItemResponseDto expectedResponse = new ItemResponseDto();
        expectedResponse.setId(itemId);
        expectedResponse.setName("Test Item");
        expectedResponse.setDescription("Test Description");
        expectedResponse.setAvailable(true);
        expectedResponse.setOwnerId(userId);

        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setId(userId);
        userResponse.setName("User Name");
        userResponse.setEmail("user@example.com");

        when(userService.getById(userId)).thenReturn(userResponse);
        when(itemRepository.findById(itemId)).thenReturn(item);
        when(itemMapper.toResponseDto(item)).thenReturn(expectedResponse);

        ItemResponseDto result = itemService.getById(itemId, userId);

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        assertEquals(expectedResponse.getName(), result.getName());
        assertEquals(expectedResponse.getDescription(), result.getDescription());
        assertEquals(expectedResponse.isAvailable(), result.isAvailable());
        assertEquals(expectedResponse.getOwnerId(), result.getOwnerId());

        verify(userService).getById(userId);
        verify(itemRepository).findById(itemId);
        verify(itemMapper).toResponseDto(item);
    }

    @Test
    void getById_WhenItemNotExists_ShouldThrowException() {
        Long itemId = 999L;
        Long userId = 1L;

        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setId(userId);
        userResponse.setName("User Name");
        userResponse.setEmail("user@example.com");

        when(userService.getById(userId)).thenReturn(userResponse);
        when(itemRepository.findById(itemId)).thenThrow(new NotFoundException("Предмет не найден"));

        assertThrows(NotFoundException.class, () -> itemService.getById(itemId, userId));

        verify(userService).getById(userId);
        verify(itemRepository).findById(itemId);
        verify(itemMapper, never()).toResponseDto(any());
    }

    @Test
    void update_WhenUserIsOwner_ShouldUpdateItem() {
        Long itemId = 1L;
        Long ownerId = 1L;

        ItemPatchDto patchDto = new ItemPatchDto();
        patchDto.setName(Optional.of("Updated Name"));
        patchDto.setDescription(Optional.of("Updated Description"));
        patchDto.setAvailable(Optional.of(false));

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Original Name");
        existingItem.setDescription("Original Description");
        existingItem.setAvailable(true);
        existingItem.setOwnerId(ownerId);

        Item updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setName("Updated Name");
        updatedItem.setDescription("Updated Description");
        updatedItem.setAvailable(false);
        updatedItem.setOwnerId(ownerId);

        ItemResponseDto expectedResponse = new ItemResponseDto();
        expectedResponse.setId(itemId);
        expectedResponse.setName("Updated Name");
        expectedResponse.setDescription("Updated Description");
        expectedResponse.setAvailable(false);
        expectedResponse.setOwnerId(ownerId);

        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setId(ownerId);
        userResponse.setName("User Name");
        userResponse.setEmail("user@example.com");

        when(userService.getById(ownerId)).thenReturn(userResponse);
        when(itemRepository.findById(itemId)).thenReturn(existingItem);
        when(itemMapper.updateItemFromPatchDto(patchDto, existingItem)).thenReturn(updatedItem);
        when(itemRepository.update(updatedItem)).thenReturn(updatedItem);
        when(itemMapper.toResponseDto(updatedItem)).thenReturn(expectedResponse);

        ItemResponseDto result = itemService.update(patchDto, itemId, ownerId);

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        assertEquals(expectedResponse.getName(), result.getName());
        assertEquals(expectedResponse.getDescription(), result.getDescription());
        assertEquals(expectedResponse.isAvailable(), result.isAvailable());
        assertEquals(expectedResponse.getOwnerId(), result.getOwnerId());

        verify(userService).getById(ownerId);
        verify(itemRepository).findById(itemId);
        verify(itemMapper).updateItemFromPatchDto(patchDto, existingItem);
        verify(itemRepository).update(updatedItem);
        verify(itemMapper).toResponseDto(updatedItem);
    }

    @Test
    void update_WhenUserIsNotOwner_ShouldThrowException() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long otherUserId = 2L;

        ItemPatchDto patchDto = new ItemPatchDto();
        patchDto.setName(Optional.of("Updated Name"));

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Original Name");
        existingItem.setDescription("Original Description");
        existingItem.setAvailable(true);
        existingItem.setOwnerId(otherUserId); // Владелец другой

        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setId(ownerId);
        userResponse.setName("User Name");
        userResponse.setEmail("user@example.com");

        when(userService.getById(ownerId)).thenReturn(userResponse);
        when(itemRepository.findById(itemId)).thenReturn(existingItem);

        assertThrows(NotOwnerException.class, () -> itemService.update(patchDto, itemId, ownerId));

        verify(userService).getById(ownerId);
        verify(itemRepository).findById(itemId);
        verify(itemMapper, never()).updateItemFromPatchDto(any(), any());
        verify(itemRepository, never()).update(any());
        verify(itemMapper, never()).toResponseDto(any());
    }

    @Test
    void search_ShouldReturnListOfItems() {
        String searchText = "test";
        Long userId = 1L;

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setDescription("Test Description 1");
        item1.setAvailable(true);
        item1.setOwnerId(userId);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Test Item 2");
        item2.setDescription("Test Description 2");
        item2.setAvailable(true);
        item2.setOwnerId(userId);

        ItemResponseDto response1 = new ItemResponseDto();
        response1.setId(1L);
        response1.setName("Test Item 1");
        response1.setDescription("Test Description 1");
        response1.setAvailable(true);
        response1.setOwnerId(userId);

        ItemResponseDto response2 = new ItemResponseDto();
        response2.setId(2L);
        response2.setName("Test Item 2");
        response2.setDescription("Test Description 2");
        response2.setAvailable(true);
        response2.setOwnerId(userId);

        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setId(userId);
        userResponse.setName("User Name");
        userResponse.setEmail("user@example.com");

        when(userService.getById(userId)).thenReturn(userResponse);
        when(itemRepository.search(searchText)).thenReturn(List.of(item1, item2));
        when(itemMapper.toResponseDto(item1)).thenReturn(response1);
        when(itemMapper.toResponseDto(item2)).thenReturn(response2);

        Collection<ItemResponseDto> result = itemService.search(searchText, userId);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(userService).getById(userId);
        verify(itemRepository).search(searchText);
        verify(itemMapper, times(2)).toResponseDto(any(Item.class));
    }

    @Test
    void getItems_ShouldReturnUserItems() {
        Long userId = 1L;

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setDescription("Test Description 1");
        item1.setAvailable(true);
        item1.setOwnerId(userId);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Test Item 2");
        item2.setDescription("Test Description 2");
        item2.setAvailable(true);
        item2.setOwnerId(userId);

        ItemResponseDto response1 = new ItemResponseDto();
        response1.setId(1L);
        response1.setName("Test Item 1");
        response1.setDescription("Test Description 1");
        response1.setAvailable(true);
        response1.setOwnerId(userId);

        ItemResponseDto response2 = new ItemResponseDto();
        response2.setId(2L);
        response2.setName("Test Item 2");
        response2.setDescription("Test Description 2");
        response2.setAvailable(true);
        response2.setOwnerId(userId);

        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setId(userId);
        userResponse.setName("User Name");
        userResponse.setEmail("user@example.com");

        when(userService.getById(userId)).thenReturn(userResponse);
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of(item1, item2));
        when(itemMapper.toResponseDto(item1)).thenReturn(response1);
        when(itemMapper.toResponseDto(item2)).thenReturn(response2);

        Collection<ItemResponseDto> result = itemService.getItems(userId);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(userService).getById(userId);
        verify(itemRepository).findByOwnerId(userId);
        verify(itemMapper, times(2)).toResponseDto(any(Item.class));
    }

    @Test
    void deleteItem_ShouldDeleteItem() {
        Long userId = 1L;
        Long itemId = 1L;

        doNothing().when(itemRepository).deleteByUserIdAndItemId(userId, itemId);

        itemService.deleteItem(userId, itemId);

        verify(itemRepository).deleteByUserIdAndItemId(userId, itemId);
    }
}