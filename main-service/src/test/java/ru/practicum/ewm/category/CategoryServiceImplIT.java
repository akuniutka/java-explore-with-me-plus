package ru.practicum.ewm.category;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WebMvcTest
@ContextConfiguration(classes = {CategoryServiceImpl.class, CategoryRepository.class})
class CategoryServiceImplIT {

    @MockBean
    private CategoryRepository repository;

    @Autowired
    private CategoryService service;

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void whenAddCategoryAndCategoryIsNull_ThenThrowException() {

        final ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> service.add(null));

        final Set<String> placesOfViolation = getPlacesOfViolation(exception);
        assertThat(placesOfViolation, contains("add.category"));
    }

    @Test
    void whenUpdateCategoryAndPatchIsNull_ThenThrowException() {

        final ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> service.update(null));

        final Set<String> placesOfViolation = getPlacesOfViolation(exception);
        assertThat(placesOfViolation, contains("update.patch"));
    }

    private Set<String> getPlacesOfViolation(final ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }
}