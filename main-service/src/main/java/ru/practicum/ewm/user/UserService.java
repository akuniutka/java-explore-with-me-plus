package ru.practicum.ewm.user;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    List<UserDto> findAll(Pageable pageable);

    List<UserDto> findByIds(List<Long> ids, Pageable pageable);

    UserDto save(NewUserRequest requestDto);

    void delete(long id);
}
