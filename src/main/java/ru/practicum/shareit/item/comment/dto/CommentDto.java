package ru.practicum.shareit.item.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentDto {

    @NotBlank(message = "Текст комментария не может быть пустым")
    @NotNull(message = "Текст комментария обязателен")
    private String text;
}