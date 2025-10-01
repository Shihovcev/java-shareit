package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public BookingResponseDto createBooking(@RequestBody @Valid BookingDto bookingDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Принял запрос на сохранение бронирования: {}, {}", bookingDto.getItemId(), userId);
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBookingStatus(@PathVariable Long bookingId,
                                                  @RequestParam Boolean approved,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Принял запрос на обновление бронирования: {}, {}", bookingId, userId);
        return bookingService.updateStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable Long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Принял запрос на вывод бронирования: {}, {}", bookingId, userId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingResponseDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Принял запрос на вывод всех броней пользователя: {}", userId);
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingResponseDto> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Принял запрос на вывод всех броней владельца: {}", userId);
        return bookingService.getOwnerBookings(userId, state, from, size);
    }
}