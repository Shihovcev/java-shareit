package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemResponseDto {
    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private Long requestId;
    private boolean available;
}
