package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingRepository {

    Booking save(Booking booking);

    Booking update(Booking booking);

    Booking getById(Long bookingId);

    Collection<Booking> getAll(Long userId);

    void deleteById(Long bookingId);
}