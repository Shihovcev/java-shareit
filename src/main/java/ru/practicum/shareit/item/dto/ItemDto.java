package ru.practicum.shareit.item.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
public class ItemDto {
    private Long id;
    @NotBlank(message = "Name must not be blank")
    private String name;
    @NotBlank(message = "Description must not be blank")
    private String description;
    @NotNull(message = "Available status must not be null")
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
