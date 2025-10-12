package ru.practicum.shareit.item.dto;

import lombok.Data;
import java.util.Optional;

@Data
public class ItemPatchDto {
    private Long id;
    private Optional<Long> ownerId = Optional.empty();
    private Optional<String> name = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<Long> requestId = Optional.empty();
    private Optional<Boolean> available = Optional.empty();
}