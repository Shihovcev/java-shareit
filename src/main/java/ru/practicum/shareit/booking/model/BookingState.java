package ru.practicum.shareit.booking.model;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.repository.BookingRepository;
import java.time.LocalDateTime;
import java.util.Collection;

public enum BookingState {
    ALL {
        @Override
        public Collection<Booking> getUserBookings(Long userId, BookingRepository repo, Pageable pageable) {
            return repo.findByBookerIdOrderByStartDesc(userId, pageable);
        }

        @Override
        public Collection<Booking> getOwnerBookings(Long userId, BookingRepository repo, Pageable pageable) {
            return repo.findByItemOwnerIdOrderByStartDesc(userId, pageable);
        }
    },

    CURRENT {
        @Override
        public Collection<Booking> getUserBookings(Long userId, BookingRepository repo, Pageable pageable) {
            return repo.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
        }

        @Override
        public Collection<Booking> getOwnerBookings(Long userId, BookingRepository repo, Pageable pageable) {
            return repo.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
        }
    },

    PAST {
        @Override
        public Collection<Booking> getUserBookings(Long userId, BookingRepository repo, Pageable pageable) {
            return repo.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable);
        }

        @Override
        public Collection<Booking> getOwnerBookings(Long userId, BookingRepository repo, Pageable pageable) {
            return repo.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable);
        }
    },

    FUTURE {
        @Override
        public Collection<Booking> getUserBookings(Long userId, BookingRepository repo, Pageable pageable) {
            return repo.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable);
        }

        @Override
        public Collection<Booking> getOwnerBookings(Long userId, BookingRepository repo, Pageable pageable) {
            return repo.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable);
        }
    },

    WAITING {
        @Override
        public Collection<Booking> getUserBookings(Long userId, BookingRepository repo, Pageable pageable) {
            return repo.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable);
        }

        @Override
        public Collection<Booking> getOwnerBookings(Long userId, BookingRepository repo, Pageable pageable) {
            return repo.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable);
        }
    },

    REJECTED {
        @Override
        public Collection<Booking> getUserBookings(Long userId, BookingRepository repo, Pageable pageable) {
            return repo.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable);
        }

        @Override
        public Collection<Booking> getOwnerBookings(Long userId, BookingRepository repo, Pageable pageable) {
            return repo.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable);
        }
    };

    public abstract Collection<Booking> getUserBookings(Long userId, BookingRepository repo, Pageable pageable);

    public abstract Collection<Booking> getOwnerBookings(Long userId, BookingRepository repo, Pageable pageable);

    public static BookingState from(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестный статус: " + state);
        }
    }
}
