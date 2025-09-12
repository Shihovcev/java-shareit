package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        User user = UserMapper.toUser(userDto);
        User saved = userRepository.save(user);
        return UserMapper.toUserDto(saved);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User current = userRepository.findById(id);
        if (current == null) {
            throw new java.util.NoSuchElementException("User not found");
        }
        String newName = current.getName();
        String newEmail = current.getEmail();
        if (userDto.getName() != null) {
            if (userDto.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Name must not be blank");
            }
            newName = userDto.getName();
        }
        if (userDto.getEmail() != null) {
            if (userDto.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email must not be blank");
            }
            newEmail = userDto.getEmail();
        }
        User toUpdate = new User();
        toUpdate.setId(id);
        toUpdate.setName(newName);
        toUpdate.setEmail(newEmail);
        User updated = userRepository.update(id, toUpdate);
        return UserMapper.toUserDto(updated);
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new java.util.NoSuchElementException("User not found");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
            .map(UserMapper::toUserDto)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }
}
