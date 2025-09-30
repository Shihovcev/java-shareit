package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface RequestService {

    ItemRequestResponseDto create(ItemRequestDto itemRequestDto, Long userId);

    ItemRequestResponseDto update(ItemRequest itemRequest);

    ItemRequestResponseDto getById(Long requestId, Long userId);

    Collection<ItemRequestResponseDto> getAll(Long userId);

    void deleteRequestBuId(Long requestId);
}
