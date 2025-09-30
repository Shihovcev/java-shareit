package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.Collection;

public interface BookingService {

    BookingResponseDto create(BookingDto bookingDto, Long userId);

    BookingResponseDto update(Long bookingId, Long userId, Boolean approved);

    BookingResponseDto getById(Long userId, Long bookingId);

    Collection<BookingResponseDto> getAll(Long userId);

    void deleteById(Long bookingId);
}
