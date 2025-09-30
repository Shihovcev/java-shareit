package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemDto {
    private Long id;
    private Long ownerId;

    @NotBlank(message = "Не указано назывыние вещи")
    private String name;

    @NotBlank(message = "Не указано описание вещи")
    private String description;
    private Long requestId;

    @NotNull(message = "Не указана занятость вещи")
    private Boolean available;
}
