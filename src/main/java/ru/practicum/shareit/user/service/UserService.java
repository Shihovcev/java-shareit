package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserResponseDto create(UserDto userDto);

    UserResponseDto getById(Long userId);

    List<UserResponseDto> getAllUsers();

    UserResponseDto update(UserPatchDto userPatchDto, Long userId);

    void deleteById(Long userId);

    User findById(Long userId);
}