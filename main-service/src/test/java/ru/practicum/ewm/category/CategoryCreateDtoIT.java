package ru.practicum.ewm.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static ru.practicum.ewm.common.TestUtils.validate;

class CategoryCreateDtoIT {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void whenNameNullOrBlank_ThenValidationErrorForName(final String name) {
        final CategoryCreateDto dto = new CategoryCreateDto(name);

        final Set<String> fieldsWithViolations = validate(dto);

        assertThat(fieldsWithViolations, contains("name"));
    }

    @Test
    void whenNameLengthExceeds50_ThenValidationErrorForName() {
        final CategoryCreateDto dto = new CategoryCreateDto("a".repeat(51));

        final Set<String> fieldsWithViolations = validate(dto);

        assertThat(fieldsWithViolations, contains("name"));
    }

    @Test
    void whenNameLength50_ThenNoValidationErrorForName() {
        final CategoryCreateDto dto = new CategoryCreateDto("a".repeat(50));

        final Set<String> fieldsWithViolations = validate(dto);

        assertThat(fieldsWithViolations, empty());
    }
}