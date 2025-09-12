package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User save(User user);

    User update(Long id, User user);

    User findById(Long id);

    List<User> findAll();

    void delete(Long id);

    boolean existsByEmail(String email);
}

