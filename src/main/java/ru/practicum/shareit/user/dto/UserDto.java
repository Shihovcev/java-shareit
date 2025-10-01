package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {

    private Long id;

    private String name;

    @Email(message = "Неверный формат электронной почты")
    @NotBlank(message = "Не указан адрес электронной почты")
    private String email;

    private String login;

    private LocalDate birthday;

    public void setName(String name) {
        this.name = (name == null || name.isBlank()) ? null : name;
    }

    public String getName() {
        return (name == null || name.isBlank()) ? login : name;
    }
}