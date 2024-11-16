package ru.practicum.ewm.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static ru.practicum.ewm.common.TestUtils.validate;

class CategoryUpdateDtoIT {

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void whenNameBlank_ThenValidationErrorForName(final String name) {
        final CategoryUpdateDto dto = new CategoryUpdateDto(name);

        final Set<String> fieldsWithViolations = validate(dto);

        assertThat(fieldsWithViolations, contains("name"));
    }

    @Test
    void whenNameLengthExceeds50_ThenValidationErrorForName() {
        final CategoryUpdateDto dto = new CategoryUpdateDto("a".repeat(51));

        final Set<String> fieldsWithViolations = validate(dto);

        assertThat(fieldsWithViolations, contains("name"));
    }

    @Test
    void whenNameLength50_ThenNoValidationErrorForName() {
        final CategoryUpdateDto dto = new CategoryUpdateDto("a".repeat(50));

        final Set<String> fieldsWithViolations = validate(dto);

        assertThat(fieldsWithViolations, empty());
    }

    @Test
    void whenNameNull_ThenNoValidationErrorForName() {
        final CategoryUpdateDto dto = new CategoryUpdateDto(null);

        final Set<String> fieldsWithViolations = validate(dto);

        assertThat(fieldsWithViolations, empty());
    }
}