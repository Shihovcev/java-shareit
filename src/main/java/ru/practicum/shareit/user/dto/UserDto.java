package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.user.model.OnCreate;
import ru.practicum.shareit.user.model.OnUpdate;

import java.time.LocalDate;

@Data
public class UserDto {
    private Long id;
    private String name;

    @Email(message = "Неверный формат электронной почты", groups = {OnCreate.class, OnUpdate.class})
    @NotBlank(message = "Не указан адрес электронной почты", groups = OnCreate.class)
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
