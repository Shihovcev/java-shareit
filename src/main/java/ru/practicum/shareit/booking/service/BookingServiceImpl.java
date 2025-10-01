package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingResponseDto create(BookingDto bookingRequestDto, Long userId) {
        log.info("Сервис сохраняет бронирование вещи {}", bookingRequestDto.getItemId());

        // Проверка корректности дат
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new ValidationException("Дата окончания должна быть позже даты старта");
        }

        if (bookingRequestDto.getStart().isEqual(bookingRequestDto.getEnd())) {
            throw new ValidationException("Дата начала и окончания не могут совпадать");
        }

        User booker = userService.findById(userId);
        Item item = itemService.findById(bookingRequestDto.getItemId());

        if (item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Владелец не может бронировать свою собственную вещь");
        }

        if (!item.isAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования");
        }

        if (bookingRepository.existsOverlappingBooking(
                item.getId(), bookingRequestDto.getStart(), bookingRequestDto.getEnd(),
                Arrays.asList(BookingStatus.APPROVED, BookingStatus.WAITING))) {
            throw new ValidationException("Вещь уже забронирована на данный период");
        }

        Booking booking = bookingMapper.toBooking(bookingRequestDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        log.info("Сервис сохранил бронирование вещи {}", booking.getItem().getId());
        return bookingMapper.toResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto updateStatus(Long bookingId, Long userId, Boolean approved) {
        log.info("Сервис обновляет бронирование вещи {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotOwnerException("Только владелец вещи или брони может подтвердить или отменить бронь");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус уже изменен");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return bookingMapper.toResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getById(Long userId, Long bookingId) {
        log.info("Сервис получает бронь вещи по id {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotOwnerException("Только владелец вещи или брони может посмотреть детали брони");
        }

        return bookingMapper.toResponseDto(booking);
    }

    @Override
    public Collection<BookingResponseDto> getUserBookings(Long userId, String state, int from, int size) {
        log.info("Сервис получает бронь пользователя {}", userId);

        userService.getById(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        Collection<Booking> bookings;

        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                        userId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new ValidationException("Неизвестный статус: " + state);
        }

        return bookings.stream()
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingResponseDto> getOwnerBookings(Long userId, String state, int from, int size) {
        log.info("Сервис получает забронированные вещи владельца {}", userId);

        userService.getById(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        Collection<Booking> bookings;

        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        userId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new ValidationException("Неизвестный статус: " + state);
        }

        return bookings.stream()
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}