package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid BookingDto bookingDto) {
        log.info("Получен запрос на бронирование от пользователя ID: {}", userId);
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto update(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        log.info("Получен запрос на обновление статуса бронирования ID: {} от пользователя ID: {}", bookingId, userId);
        return bookingService.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        log.info("Получен запрос на получение бронирования ID: {} от пользователя ID: {}", bookingId, userId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingResponseDto> getAllByBooker(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение всех бронирований пользователя ID: {}", userId);
        return bookingService.getAll(userId);
    }
}