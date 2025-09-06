package ru.practicum.shareit.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;
    private final Set<String> emailSet = new HashSet<>();

    @PostConstruct
    public void initEmailSet() {
        emailSet.clear();
        users.values().forEach(u -> emailSet.add(u.getEmail()));
    }

    @Override
    public User save(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        user.setId(nextId++);
        users.put(user.getId(), user);
        emailSet.add(user.getEmail());
        return user;
    }

    @Override
    public User update(Long id, User user) {
        User existing = users.get(id);
        if (existing == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }
        log.info("Попытка обновить пользователя id={} с email='{}' (текущий email='{}')", id, user.getEmail(), existing.getEmail());
        String oldEmail = existing.getEmail();
        String newEmail = user.getEmail();
        if (!Objects.equals(oldEmail, newEmail) && emailSet.contains(newEmail)) {
            log.warn("Email '{}' уже используется другим пользователем!", newEmail);
            throw new EmailAlreadyExistsException("Email уже существует");
        }
        user.setId(id);
        users.put(id, user);
        if (!Objects.equals(oldEmail, newEmail)) {
            emailSet.remove(oldEmail);
            emailSet.add(newEmail);
        }
        log.info("Пользователь id={} успешно обновлён. Новый email='{}'", id, user.getEmail());
        return user;
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(Long id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("User not found");
        }
        String email = users.get(id).getEmail();
        users.remove(id);
        emailSet.remove(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return emailSet.contains(email);
    }
}
