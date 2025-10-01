package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAll();

    User save(User user);

    void deleteById(Long userId);

    Optional<User> findById(Long userId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long excludedUserId);
}