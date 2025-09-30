package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Override
    public ItemResponseDto create(ItemDto itemDto, Long ownerId) {
        userService.getById(ownerId);
        log.info("Сервис вещей принял запрос на создание : {}, {}", itemDto.getName(), ownerId);
        itemDto.setOwnerId(ownerId);
        Item savedItem = itemRepository.save(itemMapper.toItem(itemDto));
        log.info("Сервис вещей сохранил вещь : {}, ид вещи {}", itemDto.getName(), savedItem.getId());
        return itemMapper.toResponseDto(savedItem);
    }

    @Override
    public ItemResponseDto getById(Long itemId, Long userId) {
        log.info("Сервис вещей принял запрос на вывод вещи : {}", itemId);
        userService.getById(userId);
        return itemMapper.toResponseDto(itemRepository.findById(itemId));
    }

    @Override
    public ItemResponseDto update(ItemPatchDto itemPatchDto, Long itemId, Long ownerId) {
        log.info("Сервис вещей принял запрос на обновление вещи : {}, владелец {}", itemPatchDto.getName(), ownerId);
        userService.getById(ownerId);
        itemPatchDto.setId(itemId);
        Item existingItem = itemRepository.findById(itemPatchDto.getId());
        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new NotOwnerException("Только владелец может редактировать вещь");
        }
        Item updatedItem = itemMapper.updateItemFromPatchDto(itemPatchDto, existingItem);
        Item savedItem = itemRepository.update(updatedItem);
        return itemMapper.toResponseDto(savedItem);
    }

    @Override
    public Collection<ItemResponseDto> search(String text, Long userId) {
        log.info("Сервис вещей принял запрос на поиск вещи : {}", text);
        userService.getById(userId);
        return itemRepository.search(text).stream()
                .map(itemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemResponseDto> getItems(Long userId) {
        log.info("Сервис вещей принял запрос на вывод вещей пользователя : {}", userId);
        userService.getById(userId);
        return itemRepository.findByOwnerId(userId).stream()
                .map(itemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        log.info("Сервис вещей принял запрос на удаление вещи : {}, пользователь {}", itemId, userId);
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }
}