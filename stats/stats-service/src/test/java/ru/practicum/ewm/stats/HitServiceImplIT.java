package ru.practicum.ewm.stats;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.ewm.stats.TestModels.END;
import static ru.practicum.ewm.stats.TestModels.ENDPOINT;
import static ru.practicum.ewm.stats.TestModels.START;

@SpringBootTest
class HitServiceImplIT {

    @MockBean
    private HitRepository repository;

    @Autowired
    private HitService service;

    @Test
    void whenAddHitAndHitIsNull_ThenThrowException() {

        final ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> service.addHit(null));

        final Set<String> placesOfViolation = getPlacesOfViolation(exception);
        assertThat(placesOfViolation, contains("addHit.hit"));
    }

    @Test
    void whenGetViewStatsAndStartIsNull_ThenThrowException() {

        final ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> service.getViewStats(null, END, List.of(ENDPOINT), false));

        final Set<String> placesOfViolation = getPlacesOfViolation(exception);
        assertThat(placesOfViolation, contains("getViewStats.start"));
    }

    @Test
    void whenGetViewStatsAndEndIsNull_ThenThrowException() {

        final ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> service.getViewStats(START, null, List.of(ENDPOINT), false));

        final Set<String> placesOfViolation = getPlacesOfViolation(exception);
        assertThat(placesOfViolation, contains("getViewStats.end"));
    }

    private Set<String> getPlacesOfViolation(final ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }
}