package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto create(UserDto userDto) {
        log.info("Сервис пользователей принял запрос на создание : {}", userDto.getName());
        if (repository.existsByEmail(userDto.getEmail())) {
            throw new EmailAlreadyExistsException("Пользователь с email " + userDto.getEmail() + " уже существует");
        }

        User savedUser = repository.save(userMapper.toUser(userDto));
        log.info("Сервис пользователей создал пользователя с Ид : {}", savedUser.getId());
        return userMapper.toResponseDto(savedUser);
    }

    @Override
    public UserResponseDto getById(Long userId) {
        log.info("Сервис пользователей принял запрос вывод пользователя по Ид : {}", userId);
        return userMapper.toResponseDto(repository.findById(userId));
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        log.info("Сервис пользователей принял запрос вывод всех пользователей");
        return repository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto update(UserDto userDto, Long userId) {
        log.info("Сервис пользователей принял запрос на обновление пользователя с Ид: {}", userId);
        if (repository.existsByEmailAndIdNot(userDto.getEmail(), userId)) {
            throw new EmailAlreadyExistsException("Пользователь с email " + userDto.getEmail() + " уже существует");
        }
        userDto.setId(userId);
        return userMapper.toResponseDto(repository.update(userMapper.toUser(userDto)));
    }

    @Override
    public void deleteById(Long userId) {
        log.info("Сервис пользователей принял запрос удаление пользователя с Ид : {}", userId);
        repository.deleteById(userId);
    }
}