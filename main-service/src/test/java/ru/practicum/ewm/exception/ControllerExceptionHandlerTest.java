package ru.practicum.ewm.exception;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.common.AbstractControllerTest;
import ru.practicum.ewm.common.LogListener;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static ru.practicum.ewm.common.TestUtils.assertLogs;

class ControllerExceptionHandlerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(ControllerExceptionHandler.class);
    private static final Clock clock = Clock.fixed(Instant.parse("2000-01-01T00:00:01Z"), ZoneId.of("Z"));

    private ControllerExceptionHandler handler;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        logListener.startListen();
        logListener.reset();
        handler = new ControllerExceptionHandler(clock);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verify(mockHttpRequest).getMethod();
        Mockito.verify(mockHttpRequest).getRequestURI();
        Mockito.verify(mockHttpRequest).getQueryString();
        Mockito.verifyNoMoreInteractions(mockHttpRequest);
        openMocks.close();
    }

    @Test
    void whenNotFoundException_ThenRespondWith404AndLog() throws Exception {
        final NotFoundException exception = new NotFoundException(Category.class, 1L);

        final ResponseEntity<Object> response = handler.handleNotFoundException(exception, mockHttpRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), equalTo(ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("The required object(s) not found")
                .message("Category with id = 1 not found")
                .timestamp(LocalDateTime.now(clock))
                .build()
        ));
        assertLogs(logListener.getEvents(), "not_found_exception.json", getClass());
    }

    @Test
    void whenNotFoundExceptionAndSeveralIds_ThenRespondWith404AndLog() throws Exception {
        final NotFoundException exception = new NotFoundException(Category.class, Set.of(1L, 2L, 3L));

        final ResponseEntity<Object> response = handler.handleNotFoundException(exception, mockHttpRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), equalTo(ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("The required object(s) not found")
                .message("Categories with id = 1, 2, 3 not found")
                .timestamp(LocalDateTime.now(clock))
                .build()
        ));
        assertLogs(logListener.getEvents(), "not_found_exception_several_ids.json", getClass());
    }
}