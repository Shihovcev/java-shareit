package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    public BookingResponseDto create(BookingDto bookingDto, Long userId) {
        bookingDto.setBooker(userService.getById(userId));
        return bookingMapper.toResponseDto(bookingRepository.save(bookingMapper.toBooking(bookingDto)));
    }

    @Override
    public BookingResponseDto update(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.getById(bookingId);

        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new NotOwnerException("Только владелец вещи может подтверждать бронирование");
        }
        booking.setBooker(userService.getById(userId));
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус бронирования уже был изменен");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.toResponseDto(bookingRepository.update(booking));
    }

    @Override
    public BookingResponseDto getById(Long userId, Long bookingId) {
        userService.getById(userId);
        return bookingMapper.toResponseDto(bookingRepository.getById(bookingId));
    }

    @Override
    public Collection<BookingResponseDto> getAll(Long userId) {
        return bookingRepository.getAll(userId).stream()
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }
}
