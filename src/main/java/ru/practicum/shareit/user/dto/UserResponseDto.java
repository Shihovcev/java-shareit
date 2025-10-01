package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private String name;
    private Long id;
    private String email;
    private String login;
}