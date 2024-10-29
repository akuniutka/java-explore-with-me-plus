package ru.practicum.ewm.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequestDto(
        @NotNull
        @Email
        String email,

        @NotNull
        @Size(max = 100)
        String name) {
}
