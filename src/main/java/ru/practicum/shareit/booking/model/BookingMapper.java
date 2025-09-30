package ru.practicum.shareit.booking.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class BookingMapper {

        public BookingResponseDto toResponseDto(Booking booking) {
            BookingResponseDto dto = new BookingResponseDto();
            dto.setId(booking.getId());
            dto.setStart(booking.getStart());
            dto.setEnd(booking.getEnd());
            dto.setStatus(booking.getStatus());

            ItemDto itemDto = new ItemDto();
            itemDto.setId(booking.getItem().getId());
            itemDto.setName(booking.getItem().getName());
            dto.setItem(itemDto);

            UserDto userDto = new UserDto();
            userDto.setId(booking.getBooker().getId());
            userDto.setName(booking.getBooker().getName());
            dto.setBooker(userDto);

            return dto;
        }

        public Booking toBooking(BookingDto bookingDto) {
            Booking booking = new Booking();
            booking.setStart(bookingDto.getStart());
            booking.setEnd(bookingDto.getEnd());
            return booking;
        }
}