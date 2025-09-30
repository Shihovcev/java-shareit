package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.RequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Override
    public ItemRequestResponseDto create(ItemRequestDto itemRequestDto, Long userId) {
        userService.getById(userId);
        return itemRequestMapper.toResponseDto(itemRequestRepository.save(
                itemRequestMapper.toItemRequest(itemRequestDto)));
    }

    @Override
    public ItemRequestResponseDto update(ItemRequest itemRequest) {
        return itemRequestMapper.toResponseDto(itemRequestRepository.update(itemRequest));
    }

    @Override
    public ItemRequestResponseDto getById(Long requestId, Long userId) {
        userService.getById(userId);
        return itemRequestMapper.toResponseDto(itemRequestRepository.getById(requestId));
    }

    @Override
    public Collection<ItemRequestResponseDto> getAll(Long userId) {
        return itemRequestRepository.getAll().stream()
                .map(itemRequestMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRequestBuId(Long requestId) {
        itemRequestRepository.deleteRequestBuId(requestId);
    }
}
