package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import java.util.Collection;

public interface BookingService {

    BookingResponseDto create(BookingDto bookingRequestDto, Long userId);

    BookingResponseDto updateStatus(Long bookingId, Long userId, Boolean approved);

    BookingResponseDto getById(Long userId, Long bookingId);

    Collection<BookingResponseDto> getUserBookings(Long userId, String state, int from, int size);

    Collection<BookingResponseDto> getOwnerBookings(Long userId, String state, int from, int size);
}