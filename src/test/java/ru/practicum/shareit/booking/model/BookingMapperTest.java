package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapper();

    @Test
    void toResponseDto_ShouldMapCorrectly() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        var result = bookingMapper.toResponseDto(booking);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(booking.getItem().getId(), result.getItem().getId());
        assertEquals(booking.getBooker().getId(), result.getBooker().getId());
    }

    @Test
    void toBooking_ShouldMapCorrectly() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        var result = bookingMapper.toBooking(bookingDto);

        assertNotNull(result);
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
    }
}