package ru.practicum.ewm.user;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    List<UserResponseDto> findAll(Pageable pageable);

    List<UserResponseDto> findByIds(List<Long> ids, Pageable pageable);

    UserResponseDto save(UserRequestDto requestDto);

    void delete(long id);
}
