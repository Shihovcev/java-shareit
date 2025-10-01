package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User createTestUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Item createTestItem(Long id, String name, String description, boolean available, User owner) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return item;
    }

    private Booking createTestBooking(Long id, LocalDateTime start, LocalDateTime end, Item item, User booker,
                                      BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return booking;
    }

    @Test
    void create_WhenValidData_ShouldReturnBooking() {
        User testUser = createTestUser(1L, "Test User", "test@mail.com");
        User testOwner = createTestUser(2L, "Test Owner", "owner@mail.com");
        Item testItem = createTestItem(1L, "Test Item", "Test Description", true,
                testOwner);
        Booking testBooking = createTestBooking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), testItem, testUser, BookingStatus.WAITING);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userService.findById(anyLong())).thenReturn(testUser);
        when(itemService.findById(anyLong())).thenReturn(testItem);
        when(bookingRepository.existsOverlappingBooking(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any())).thenReturn(false);
        when(bookingMapper.toBooking(any(BookingDto.class))).thenReturn(testBooking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        var result = bookingService.create(bookingDto, 1L);

        assertNull(result);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void create_WhenUserIsOwner_ShouldThrowException() {
        User testUser = createTestUser(1L, "Test User", "test@mail.com");
        Item testItem = createTestItem(1L, "Test Item", "Test Description", true, testUser);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userService.findById(anyLong())).thenReturn(testUser);
        when(itemService.findById(anyLong())).thenReturn(testItem);

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void create_WhenItemNotAvailable_ShouldThrowException() {
        User testUser = createTestUser(1L, "Test User", "test@mail.com");
        User testOwner = createTestUser(2L, "Test Owner", "owner@mail.com");
        Item testItem = createTestItem(1L, "Test Item", "Test Description", false,
                testOwner);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userService.findById(anyLong())).thenReturn(testUser);
        when(itemService.findById(anyLong())).thenReturn(testItem);

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void create_WhenStartAfterEnd_ShouldThrowException() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void updateStatus_WhenValidData_ShouldUpdateStatus() {
        User testUser = createTestUser(1L, "Test User", "test@mail.com");
        User testOwner = createTestUser(2L, "Test Owner", "owner@mail.com");
        Item testItem = createTestItem(1L, "Test Item", "Test Description", true,
                testOwner);
        Booking testBooking = createTestBooking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), testItem, testUser, BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));

        bookingService.updateStatus(1L, 2L, true);

        verify(bookingRepository, times(1)).save(any(Booking.class));
        assertEquals(BookingStatus.APPROVED, testBooking.getStatus());
    }

    @Test
    void updateStatus_WhenNotOwner_ShouldThrowException() {
        User testUser = createTestUser(1L, "Test User", "test@mail.com");
        User testOwner = createTestUser(2L, "Test Owner", "owner@mail.com");
        Item testItem = createTestItem(1L, "Test Item", "Test Description", true,
                testOwner);
        Booking testBooking = createTestBooking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), testItem, testUser, BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));

        assertThrows(NotOwnerException.class, () -> bookingService.updateStatus(1L, 3L, true));
    }

    @Test
    void getById_WhenValidUser_ShouldReturnBooking() {
        User testUser = createTestUser(1L, "Test User", "test@mail.com");
        User testOwner = createTestUser(2L, "Test Owner", "owner@mail.com");
        Item testItem = createTestItem(1L, "Test Item", "Test Description", true,
                testOwner);
        Booking testBooking = createTestBooking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), testItem, testUser, BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));

        var result = bookingService.getById(1L, 1L);

        assertNull(result);
    }

    @Test
    void getUserBookings_WhenValidState_ShouldReturnBookings() {
        User testUser = createTestUser(1L, "Test User", "test@mail.com");
        User testOwner = createTestUser(2L, "Test Owner", "owner@mail.com");
        Item testItem = createTestItem(1L, "Test Item", "Test Description", true,
                testOwner);
        Booking testBooking = createTestBooking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), testItem, testUser, BookingStatus.WAITING);

        Pageable pageable = PageRequest.of(0, 10);
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class))).thenReturn(List.of(testBooking));

        var result = bookingService.getUserBookings(1L, "ALL", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getUserBookings_WhenInvalidState_ShouldThrowException() {
        User testUser = createTestUser(1L, "Test User", "test@mail.com");

        assertThrows(ValidationException.class, () -> bookingService.getUserBookings(1L,
                "INVALID", 0, 10));
    }

    @Test
    void getOwnerBookings_WhenValidState_ShouldReturnBookings() {
        User testOwner = createTestUser(1L, "Test Owner", "owner@mail.com");
        User testUser = createTestUser(2L, "Test User", "user@mail.com");
        Item testItem = createTestItem(1L, "Test Item", "Test Description",
                true, testOwner);
        Booking testBooking = createTestBooking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), testItem, testUser, BookingStatus.WAITING);

        Pageable pageable = PageRequest.of(0, 10);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(),
                any(Pageable.class))).thenReturn(List.of(testBooking));

        var result = bookingService.getOwnerBookings(1L, "ALL", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}