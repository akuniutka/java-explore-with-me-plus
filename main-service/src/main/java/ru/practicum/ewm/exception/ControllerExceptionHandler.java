package ru.practicum.ewm.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;

@RestControllerAdvice
class ControllerExceptionHandler extends BaseExceptionHandler {

    ControllerExceptionHandler(final Clock clock) {
        super(clock);
    }
}
