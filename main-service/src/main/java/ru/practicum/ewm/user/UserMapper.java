package ru.practicum.ewm.user;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    User mapToUser(UserRequestDto dto) {
        if (dto == null) {
            return null;
        }
        final User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        return user;
    }

    UserResponseDto mapToDto(final User user) {
        if (user == null) {
            return null;
        }
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    List<UserResponseDto> mapToDto(final List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(this::mapToDto)
                .toList();
    }
}
