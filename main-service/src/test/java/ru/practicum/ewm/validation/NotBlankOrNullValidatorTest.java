package ru.practicum.ewm.validation;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class NotBlankOrNullValidatorTest {

    @Test
    void whenValueIsNull_ThenAcceptItAsValid() {

        final boolean isValid = new NotBlankOrNullValidator().isValid(null, null);

        assertThat(isValid, is(true));
    }

    @Test
    void whenValueIsEmpty_ThenRejectItAsInvalid() {

        final boolean isValid = new NotBlankOrNullValidator().isValid("", null);

        assertThat(isValid, is(false));
    }

    @Test
    void whenValueIsBlank_ThenRejectItAsInvalid() {

        final boolean isValid = new NotBlankOrNullValidator().isValid(" ", null);

        assertThat(isValid, is(false));
    }

    @Test
    void whenValueNotBlank_ThenAcceptIsAsValid() {

        final boolean isValid = new NotBlankOrNullValidator().isValid("test", null);

        assertThat(isValid, is(true));
    }
}