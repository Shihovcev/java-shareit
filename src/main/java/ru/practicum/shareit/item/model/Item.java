package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class Item {
    private Long id;
    private Long ownerId;
    private String url;
    private String name;
    private String description;
    private Long requestId;
    private boolean available;
}