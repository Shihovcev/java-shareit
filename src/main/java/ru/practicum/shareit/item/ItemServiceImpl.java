package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId);
        if (owner == null) throw new java.util.NoSuchElementException("User not found");
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item saved = itemRepository.save(item);
        return ItemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long ownerId) {
        Item existing = itemRepository.findById(itemId);
        if (existing == null) throw new java.util.NoSuchElementException("Item not found");
        if (existing.getOwner() == null || !existing.getOwner().getId().equals(ownerId)) {
            throw new java.util.NoSuchElementException("Item not found");
        }
        if (itemDto.getName() != null) existing.setName(itemDto.getName());
        if (itemDto.getDescription() != null) existing.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) existing.setAvailable(itemDto.getAvailable());
        Item updated = itemRepository.update(itemId, existing);
        return ItemMapper.toItemDto(updated);
    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = itemRepository.findById(itemId);
        if (item == null) throw new java.util.NoSuchElementException("Item not found");
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId).stream()
            .map(ItemMapper::toItemDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text).stream()
            .map(ItemMapper::toItemDto)
            .collect(Collectors.toList());
    }
}
