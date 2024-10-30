package ru.practicum.ewm.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;
import java.time.LocalDateTime;

@RestControllerAdvice
class ControllerExceptionHandler extends BaseExceptionHandler {

    ControllerExceptionHandler(final Clock clock) {
        super(clock);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleNotFoundException(
            final NotFoundException exception,
            final HttpServletRequest httpRequest) {
        log.warn(exception.getMessage());
        final ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("The required object was not found")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now(clock))
                .build();
        logHttpResponse(httpRequest, apiError);
        return new ResponseEntity<>(apiError, apiError.status());
    }
}
