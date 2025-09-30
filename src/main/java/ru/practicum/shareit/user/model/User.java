package ru.practicum.shareit.user.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String email;
    private String name;
    private String login;
    private final LocalDateTime registrationDate = LocalDateTime.now();
    private LocalDate birthday;
}