package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.CommentNotAllowedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.model.CommentMapper;
import ru.practicum.shareit.item.comment.repository.CommentsRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final CommentsRepository commentsRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public ItemResponseDto create(ItemDto itemDto, Long ownerId) {
        userService.getById(ownerId);
        User user = new User();
        user.setId(ownerId);
        log.info("Сервис вещей принял запрос на создание : {}, {}", itemDto.getName(), ownerId);
        itemDto.setOwnerId(user.getId());
        Item savedItem = itemRepository.save(itemMapper.toItem(itemDto, user));
        log.info("Сервис вещей сохранил вещь : {}, ид вещи {}", itemDto.getName(), savedItem.getId());
        return itemMapper.toResponseDto(savedItem);
    }

    @Override
    public ItemResponseDto getById(Long itemId, Long userId) {
        log.info("Сервис вещей принял запрос на вывод вещи : {}", itemId);
        userService.getById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));

        ItemResponseDto responseDto = itemMapper.toResponseDto(item);

        if (item.getOwner().getId().equals(userId)) {
            Optional<Booking> lastBooking = bookingRepository.findLastBooking(itemId, BookingStatus.APPROVED,
                    LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findNextBooking(itemId, BookingStatus.APPROVED,
                    LocalDateTime.now());
            lastBooking.ifPresent(booking -> responseDto.setLastBooking(bookingMapper.toResponseDto(booking)));
            nextBooking.ifPresent(booking -> responseDto.setNextBooking(bookingMapper.toResponseDto(booking)));
        } else {
            responseDto.setLastBooking(null);
            responseDto.setNextBooking(null);
        }

        Collection<CommentResponseDto> comments = findAllByItemId(itemId);

        responseDto.setComments(new ArrayList<>(comments));

        return responseDto;
    }

    @Override
    public ItemResponseDto update(ItemPatchDto itemPatchDto, Long itemId, Long ownerId) {
        log.info("Сервис вещей принял запрос на обновление вещи: {}, владелец {}", itemPatchDto.getName(), ownerId);

        userService.getById(ownerId);
        itemPatchDto.setId(itemId);

        Optional<Item> existingItemOpt = itemRepository.findById(itemId);
        if (existingItemOpt.isEmpty()) {
            throw new NotFoundException("Вещь с ID " + itemId + " не найдена");
        }

        Item existingItem = existingItemOpt.get();

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotOwnerException("Только владелец может редактировать вещь");
        }

        Item updatedItem = itemMapper.updateItemFromPatchDto(itemPatchDto, existingItem);
        Item savedItem = itemRepository.save(updatedItem);
        return itemMapper.toResponseDto(savedItem);
    }

    @Override
    public Collection<ItemResponseDto> search(String text, Long userId) {
        log.info("Сервис вещей принял запрос на поиск вещи : {}", text);
        userService.getById(userId);

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        Collection<Item> items = itemRepository.searchAvailableItems(text);
        return items.stream()
                .map(item -> {
                    ItemResponseDto dto = itemMapper.toResponseDto(item);
                    dto.setComments(Collections.emptyList());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemResponseDto> getItems(Long userId) {
        log.info("Сервис вещей принял запрос на вывод вещей пользователя : {}", userId);
        userService.getById(userId);
        Collection<Item> items = itemRepository.findByOwnerId(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        // Получаем все бронирования и комментарии одним запросом
        List<Booking> bookings = bookingRepository.findAllByItemIdIn(itemIds);
        List<Comment> comments = commentsRepository.findAllByItemIdIn(itemIds);

        // Группируем бронирования по itemId
        Map<Long, List<Booking>> bookingsByItemId = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        // Группируем комментарии по itemId
        Map<Long, List<Comment>> commentsByItemId = comments.stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        return items.stream()
                .map(item -> {
                    ItemResponseDto dto = itemMapper.toResponseDto(item);

                    // Для владельца показываем информацию о бронированиях
                    List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), Collections.emptyList());
                    Optional<Booking> lastBooking = itemBookings.stream()
                            .filter(b -> b.getStatus() == BookingStatus.APPROVED && b.getStart().isBefore(now))
                            .max(Comparator.comparing(Booking::getStart));
                    Optional<Booking> nextBooking = itemBookings.stream()
                            .filter(b -> b.getStatus() == BookingStatus.APPROVED && b.getStart().isAfter(now))
                            .min(Comparator.comparing(Booking::getStart));
                    lastBooking.ifPresent(booking -> dto.setLastBooking(bookingMapper.toResponseDto(booking)));
                    nextBooking.ifPresent(booking -> dto.setNextBooking(bookingMapper.toResponseDto(booking)));

                    List<Comment> itemComments = commentsByItemId.getOrDefault(item.getId(), Collections.emptyList());
                    List<CommentResponseDto> commentDtos = itemComments.stream()
                            .map(commentMapper::fromComment)
                            .collect(Collectors.toList());
                    dto.setComments(commentDtos);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId, Long userId) {
        log.info("Сервис вещей принял запрос на удаление вещи : {}, пользователь {}", itemId, userId);
        itemRepository.deleteByIdAndOwnerId(itemId, userId);
    }

    @Override
    public Item findById(Long itemId) {
        log.info("Ищу вещь в базе для бронирования");
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            return item.get();
        } else {
            throw new NotFoundException("Вещь не найдена");
        }
    }

    @Override
    public CommentResponseDto save(Long userId, Long itemId, CommentDto commentDto) {
        log.info("Сохраняю комментарии {}, {}", userId, itemId);
        User author = userService.findById(userId);
        Item item = findById(itemId);

        boolean hasCompletedBooking = bookingRepository.existsCompletedBookingByUserAndItem(userId,
                itemId, BookingStatus.APPROVED, LocalDateTime.now());

        if (!hasCompletedBooking) {
            throw new CommentNotAllowedException("Данная вещь не бронировалась пользователем");
        }

        Comment comment = commentMapper.toEntity(commentDto, author, item);
        Comment savedComment = commentsRepository.save(comment);

        return commentMapper.fromComment(savedComment);
    }

    @Override
    public Collection<CommentResponseDto> findAllByItemId(Long itemId) {
        log.info("Звгружаю комментарии для вещи {}", itemId);
        return commentsRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::fromComment)
                .collect(Collectors.toList());
    }
}