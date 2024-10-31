package ru.practicum.ewm.user;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class UserTestUtil {
    static final Pageable PAGEABLE = PageRequest.of(0, 10);
    static final long USER_ID_1 = 1L;
    static final long USER_ID_2 = 2L;
    static final long USER_ID_3 = 3L;
    static final String USER_NAME_1 = "First User";
    static final String USER_NAME_2 = "Second User";
    static final String USER_NAME_3 = "Third User";
    static final String EMAIL_1 = "first@test.com";
    static final String EMAIL_2 = "second@test.com";
    static final String EMAIL_3 = "third@test.com";
}
