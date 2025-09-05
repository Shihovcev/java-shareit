package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestBody @jakarta.validation.Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId, @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.update(itemId, itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getAllByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        if (text == null || text.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return itemService.search(text);
    }
}
