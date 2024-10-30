package ru.practicum.ewm.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.ewm.common.HttpRequestResponseLogger;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseExceptionHandler extends HttpRequestResponseLogger {

    protected final Clock clock;

    protected BaseExceptionHandler(final Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException exception,
            final HttpServletRequest httpRequest) {
        log.warn(exception.getMessage(), exception);
        final List<FieldErrorData> errors = exception.getFieldErrors().stream()
                .map(error -> new FieldErrorData(error.getField(), error.getDefaultMessage(), error.getRejectedValue()))
                .toList();
        final Set<String> fields = errors.stream()
                .map(FieldErrorData::field)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        final ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Wrong request format")
                .message(makeMessage(fields, errors))
                .errors(errors)
                .timestamp(LocalDateTime.now(clock))
                .build();
        logHttpResponse(httpRequest, apiError);
        return new ResponseEntity<>(apiError, apiError.status());
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

    private String makeMessage(final Set<String> fields, final List<FieldErrorData> errors) {
        if (fields.size() == 1 && errors.size() == 1) {
            return "There is error in field " + fields.iterator().next();
        } else if (fields.size() == 1) {
            return "There are errors in field " + fields.iterator().next();
        } else {
            return "There are errors in fields " + String.join(", ", fields);
        }
    }

    private record FieldErrorData(String field, String error, Object value) {

    }
}
