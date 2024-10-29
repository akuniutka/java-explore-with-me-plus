package ru.practicum.ewm.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.ewm.common.HttpRequestResponseLogger;

import java.time.Clock;
import java.time.LocalDateTime;

public abstract class BaseExceptionHandler extends HttpRequestResponseLogger {

    protected final Clock clock;

    protected BaseExceptionHandler(final Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleException(final Exception exception, final HttpServletRequest httpRequest) {
        log.error(exception.getMessage(), exception);
        final ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .reason("Unexpected error")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now(clock))
                .build();
        logHttpResponse(httpRequest, apiError);
        return new ResponseEntity<>(apiError, apiError.status());
    }
}
