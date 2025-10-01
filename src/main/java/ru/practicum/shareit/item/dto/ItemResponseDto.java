package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;

import java.util.List;

@Data
public class ItemResponseDto {
    private Long id;
    private Long owner;
    private String name;
    private String description;
    private Long requestId;
    private boolean available;
    private BookingResponseDto lastBooking;
    private BookingResponseDto nextBooking;
    private List<CommentResponseDto> comments;
}