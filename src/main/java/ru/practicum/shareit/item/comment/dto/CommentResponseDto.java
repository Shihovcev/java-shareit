package ru.practicum.shareit.item.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {

    private Long id;
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime created;
}