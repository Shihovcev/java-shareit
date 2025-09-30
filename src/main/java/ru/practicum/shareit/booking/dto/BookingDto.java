package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserResponseDto booker;
}
