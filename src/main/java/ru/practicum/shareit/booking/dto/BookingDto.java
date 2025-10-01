package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    @NotNull(message = "Идентификатор вещи не может быть Null")
    private Long itemId;

    @FutureOrPresent(message = "Дата начала должна быть в будущем или текущем времени")
    private LocalDateTime start;

    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;

    private User booker;
}