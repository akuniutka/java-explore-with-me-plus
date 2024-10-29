package ru.practicum.ewm.user;

import lombok.Builder;

@Builder
public record UserResponseDto(
        long id,
        String email,
        String name) {
}
