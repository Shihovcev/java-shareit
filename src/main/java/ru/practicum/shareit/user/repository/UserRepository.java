package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {
    Collection<User> findAll();

    User save(User user);

    User update(User userDto);

    public void deleteById(Long userId);

    User findById(Long userId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long excludedUserId);
}