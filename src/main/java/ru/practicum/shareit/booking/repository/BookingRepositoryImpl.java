package ru.practicum.shareit.booking.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Repository
public class BookingRepositoryImpl implements BookingRepository {
    private static final Set<Booking> bookings = new HashSet<>();
    private final AtomicLong generateIds = new AtomicLong(0);

    @Override
    public Booking save(Booking booking) {
        booking.setId(generateIds.incrementAndGet());
        log.info("Сохраняю бронь : {}", booking.getId());
        bookings.add(booking);
        return booking;
    }

    @Override
    public Booking update(Booking booking) {
        return booking;
    }

    @Override
    public Booking getById(Long bookingId) {
        return bookings.stream()
                .filter(booking -> booking.getId().equals(bookingId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));
    }

    @Override
    public Collection<Booking> getAll(Long userId) {
        return new ArrayList<>();
    }

    @Override
    public void deleteById(Long bookingId) {

    }
}
