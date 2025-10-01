package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import java.time.LocalDate;
import java.util.Optional;

@Data
public class UserPatchDto {
    private Optional<Long> id = Optional.empty();
    private Optional<String> name = Optional.empty();
    @Email(message = "Неверный формат электронной почты")
    private Optional<String> email = Optional.empty();
    private Optional<String> login = Optional.empty();
    private Optional<LocalDate> birthday = Optional.empty();
}
