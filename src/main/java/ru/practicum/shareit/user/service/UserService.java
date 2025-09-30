package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto create(UserDto userDto);

    public UserResponseDto getById(Long userId);

    List<UserResponseDto> getAllUsers();

    UserResponseDto update(UserDto userDto, Long userId);

    void deleteById(Long userId);
}