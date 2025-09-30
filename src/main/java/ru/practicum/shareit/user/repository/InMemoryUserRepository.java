package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Set<User> USERS = new HashSet<>();
    private final AtomicLong generateId = new AtomicLong(0);

    @Override
    public User save(User user) {
        user.setId(generateId.incrementAndGet());
        USERS.add(user);
        log.info("Пользователь сохранен: {}", user.getId());
        return user;
    }

    public User findById(Long userId) {
        log.info("Ищу пользователя по Ид : {}", userId);
        return USERS.stream()
                .filter(user -> Objects.equals(user.getId(), userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public Collection<User> findAll() {
        log.info("Ищу всех пользователей");
        return USERS.stream().toList();
    }

    @Override
    public User update(User user) {
        log.info("Обновляю пользователя по Ид : {}", user.getId());
        User userFromMemory = findById(user.getId());

        if (user.getName() != null && !user.getName().isEmpty()) {
            userFromMemory.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            userFromMemory.setEmail(user.getEmail());
        }
        return userFromMemory;
    }

    @Override
    public void deleteById(Long userId) {
        log.info("Удаляю пользователя по Ид : {}", userId);
        findById(userId);
        USERS.removeIf(user -> user.getId().equals(userId));
    }

    @Override
    public boolean existsByEmail(String email) {
        return USERS.stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long excludedUserId) {
        return USERS.stream()
                .anyMatch(user ->
                        user.getEmail().equalsIgnoreCase(email) &&
                                !user.getId().equals(excludedUserId)
                );
    }
}