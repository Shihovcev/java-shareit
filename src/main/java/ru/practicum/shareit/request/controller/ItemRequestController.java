package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestResponseDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание нового запроса от пользователя ID: {}", userId);
        return requestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public Collection<ItemRequestResponseDto> getAllByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение всех запросов пользователя ID: {}", userId);
        return requestService.getAll(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestResponseDto> getAll(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение всех запросов от пользователя ID: {}", userId);
        return requestService.getAll(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        log.info("Получен запрос на получение запроса ID: {} от пользователя ID: {}", requestId, userId);
        return requestService.getById(requestId, userId);
    }
}
