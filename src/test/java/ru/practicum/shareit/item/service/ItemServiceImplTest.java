package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.model.CommentMapper;
import ru.practicum.shareit.item.comment.repository.CommentsRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserService userService;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentsRepository commentsRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void create_ShouldCreateItemAndReturnResponseDto() {
        Long ownerId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        User user = new User();
        user.setId(ownerId);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);

        ItemResponseDto expectedResponse = new ItemResponseDto();
        expectedResponse.setId(1L);
        expectedResponse.setName("Test Item");
        expectedResponse.setDescription("Test Description");
        expectedResponse.setAvailable(true);

        when(userService.getById(ownerId)).thenReturn(null);
        when(itemMapper.toItem(any(ItemDto.class), any(User.class))).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toResponseDto(any(Item.class))).thenReturn(expectedResponse);

        ItemResponseDto result = itemService.create(itemDto, ownerId);

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        assertEquals(expectedResponse.getName(), result.getName());
        verify(userService).getById(ownerId);
        verify(itemMapper).toItem(any(ItemDto.class), any(User.class));
        verify(itemRepository).save(any(Item.class));
        verify(itemMapper).toResponseDto(any(Item.class));
    }

    @Test
    void getById_WhenItemExistsAndUserIsOwner_ShouldReturnItemWithBookings() {
        Long itemId = 1L;
        Long userId = 1L;

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setName("Test Item");
        item.setOwner(owner);

        Booking lastBooking = new Booking();
        Booking nextBooking = new Booking();

        ItemResponseDto responseDto = new ItemResponseDto();
        responseDto.setId(itemId);
        responseDto.setName("Test Item");

        when(userService.getById(userId)).thenReturn(null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toResponseDto(any(Item.class))).thenReturn(responseDto);
        when(bookingRepository.findLastBooking(eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findNextBooking(eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.of(nextBooking));
        when(commentsRepository.findAllByItemId(itemId)).thenReturn(List.of());

        ItemResponseDto result = itemService.getById(itemId, userId);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        verify(bookingRepository).findLastBooking(eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class));
        verify(bookingRepository).findNextBooking(eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class));
    }

    @Test
    void getById_WhenItemExistsAndUserIsNotOwner_ShouldReturnItemWithoutBookings() {
        Long itemId = 1L;
        Long userId = 2L;
        Long ownerId = 1L;

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(itemId);
        item.setName("Test Item");
        item.setOwner(owner);

        ItemResponseDto responseDto = new ItemResponseDto();
        responseDto.setId(itemId);

        when(userService.getById(userId)).thenReturn(null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toResponseDto(any(Item.class))).thenReturn(responseDto);
        when(commentsRepository.findAllByItemId(itemId)).thenReturn(List.of());

        ItemResponseDto result = itemService.getById(itemId, userId);

        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        verify(bookingRepository, never()).findLastBooking(anyLong(), any(), any());
        verify(bookingRepository, never()).findNextBooking(anyLong(), any(), any());
    }

    @Test
    void getById_WhenItemNotExists_ShouldThrowNotFoundException() {
        Long itemId = 999L;
        Long userId = 1L;

        when(userService.getById(userId)).thenReturn(null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(itemId, userId));

        verify(itemRepository).findById(itemId);
        verify(itemMapper, never()).toResponseDto(any());
    }

    @Test
    void update_WhenUserIsOwner_ShouldUpdateItem() {
        Long itemId = 1L;
        Long ownerId = 1L;

        ItemPatchDto patchDto = new ItemPatchDto();
        patchDto.setName(Optional.of("Updated Name"));

        User owner = new User();
        owner.setId(ownerId);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Old Name");
        existingItem.setOwner(owner);

        Item updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setName("Updated Name");
        updatedItem.setOwner(owner);

        ItemResponseDto expectedResponse = new ItemResponseDto();
        expectedResponse.setId(itemId);
        expectedResponse.setName("Updated Name");

        when(userService.getById(ownerId)).thenReturn(null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemMapper.updateItemFromPatchDto(any(ItemPatchDto.class), any(Item.class))).thenReturn(updatedItem);
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);
        when(itemMapper.toResponseDto(any(Item.class))).thenReturn(expectedResponse);

        ItemResponseDto result = itemService.update(patchDto, itemId, ownerId);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void update_WhenUserIsNotOwner_ShouldThrowNotOwnerException() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long otherUserId = 2L;

        ItemPatchDto patchDto = new ItemPatchDto();

        User owner = new User();
        owner.setId(ownerId);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setOwner(owner);

        when(userService.getById(otherUserId)).thenReturn(null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        assertThrows(NotOwnerException.class, () -> itemService.update(patchDto, itemId, otherUserId));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void search_WhenTextIsBlank_ShouldReturnEmptyList() {
        Long userId = 1L;

        when(userService.getById(userId)).thenReturn(null);

        Collection<ItemResponseDto> result = itemService.search("", userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).searchAvailableItems(anyString());
    }

    @Test
    void search_WhenTextIsNotNull_ShouldReturnItems() {
        Long userId = 1L;
        String searchText = "test";

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        ItemResponseDto responseDto = new ItemResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Test Item");

        when(userService.getById(userId)).thenReturn(null);
        when(itemRepository.searchAvailableItems(searchText)).thenReturn(List.of(item));
        when(itemMapper.toResponseDto(any(Item.class))).thenReturn(responseDto);

        Collection<ItemResponseDto> result = itemService.search(searchText, userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemRepository).searchAvailableItems(searchText);
    }

    @Test
    void getItems_ShouldReturnUserItemsWithBookingsAndComments() {
        Long userId = 1L;

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);

        Booking lastBooking = new Booking();
        Booking nextBooking = new Booking();
        Comment comment = new Comment();

        ItemResponseDto responseDto = new ItemResponseDto();
        responseDto.setId(1L);

        when(userService.getById(userId)).thenReturn(null);
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of(item));
        when(itemMapper.toResponseDto(any(Item.class))).thenReturn(responseDto);
        when(bookingRepository.findLastBooking(anyLong(), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findNextBooking(anyLong(), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.of(nextBooking));
        when(commentsRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));
        when(commentMapper.fromComment(any(Comment.class))).thenReturn(new CommentResponseDto());

        Collection<ItemResponseDto> result = itemService.getItems(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemRepository).findByOwnerId(userId);
    }

    @Test
    void getItems_WhenNoItems_ShouldReturnEmptyList() {
        Long userId = 1L;

        when(userService.getById(userId)).thenReturn(null);
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of());

        Collection<ItemResponseDto> result = itemService.getItems(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository).findByOwnerId(userId);
    }

    @Test
    void deleteItem_ShouldDeleteItem() {
        Long userId = 1L;
        Long itemId = 1L;

        doNothing().when(itemRepository).deleteByIdAndOwnerId(userId, itemId);

        itemService.deleteItem(itemId, userId);

        verify(itemRepository).deleteByIdAndOwnerId(userId, itemId);
    }

    @Test
    void findById_WhenItemExists_ShouldReturnItem() {
        Long itemId = 1L;
        Item expectedItem = new Item();
        expectedItem.setId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        Item result = itemService.findById(itemId);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
    }

    @Test
    void findById_WhenItemNotExists_ShouldThrowNotFoundException() {
        Long itemId = 999L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findById(itemId));
    }

    @Test
    void findAllByItemId_ShouldReturnComments() {
        Long itemId = 1L;

        Comment comment = new Comment();
        comment.setText("Test comment");

        CommentResponseDto responseDto = new CommentResponseDto();
        responseDto.setText("Test comment");

        when(commentsRepository.findAllByItemId(itemId)).thenReturn(List.of(comment));
        when(commentMapper.fromComment(any(Comment.class))).thenReturn(responseDto);

        Collection<CommentResponseDto> result = itemService.findAllByItemId(itemId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test comment", result.iterator().next().getText());
    }
}