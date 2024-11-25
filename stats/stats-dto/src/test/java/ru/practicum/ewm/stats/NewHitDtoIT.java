package ru.practicum.ewm.stats;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static ru.practicum.ewm.common.TestUtils.validate;

class NewHitDtoIT {

    static final String APP = "mainService";
    static final String ENDPOINT = "endpointA";
    static final String IP = "127.0.0.1";
    static final LocalDateTime TIMESTAMP = LocalDateTime.of(2000, Month.JANUARY, 31, 13, 30, 55);

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void whenAppNullOrBlank_ThenValidationErrorForApp(final String app) {
        final NewHitDto dto = new NewHitDto(app, ENDPOINT, IP, TIMESTAMP);

        final Set<String> fieldsWithViolation = validate(dto);

        assertThat(fieldsWithViolation, contains("app"));
    }

    @Test
    void whenAppLengthExceeds255_ThenValidationErrorForApp() {
        final NewHitDto dto = new NewHitDto("a".repeat(256), ENDPOINT, IP, TIMESTAMP);

        final Set<String> fieldsWithViolation = validate(dto);

        assertThat(fieldsWithViolation, contains("app"));
    }

    @Test
    void whenAppLength255_ThenNoValidationErrorForApp() {
        final NewHitDto dto = new NewHitDto("a".repeat(255), ENDPOINT, IP, TIMESTAMP);

        final Set<String> fieldsWithViolations = validate(dto);

        assertThat(fieldsWithViolations, empty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void whenUriNullOrBlank_ThenValidationErrorForUri(final String uri) {
        final NewHitDto dto = new NewHitDto(APP, uri, IP, TIMESTAMP);

        final Set<String> fieldsWithViolation = validate(dto);

        assertThat(fieldsWithViolation, contains("uri"));
    }

    @Test
    void whenUriLengthExceeds512_ThenValidationErrorForUri() {
        final NewHitDto dto = new NewHitDto(APP, "a".repeat(513), IP, TIMESTAMP);

        final Set<String> fieldsWithViolation = validate(dto);

        assertThat(fieldsWithViolation, contains("uri"));
    }

    @Test
    void whenUriLength512_ThenNoValidationErrorForUri() {
        final NewHitDto dto = new NewHitDto(APP, "a".repeat(512), IP, TIMESTAMP);

        final Set<String> fieldsWithViolations = validate(dto);

        assertThat(fieldsWithViolations, empty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void whenIpNullOrBlank_ThenValidationErrorForIp(final String ip) {
        final NewHitDto dto = new NewHitDto(APP, ENDPOINT, ip, TIMESTAMP);

        final Set<String> fieldsWithViolation = validate(dto);

        assertThat(fieldsWithViolation, contains("ip"));
    }

    @Test
    void whenIpLengthExceeds40_ThenValidationErrorForIp() {
        final NewHitDto dto = new NewHitDto(APP, ENDPOINT, "a".repeat(41), TIMESTAMP);

        final Set<String> fieldsWithViolation = validate(dto);

        assertThat(fieldsWithViolation, contains("ip"));
    }

    @Test
    void whenIpLength40_ThenNoValidationErrorForIp() {
        final NewHitDto dto = new NewHitDto(APP, ENDPOINT, "a".repeat(40), TIMESTAMP);

        final Set<String> fieldsWithViolations = validate(dto);

        assertThat(fieldsWithViolations, empty());
    }

    @Test
    void whenTimestampNull_ThenValidationErrorForTimestamp() {
        final NewHitDto dto = new NewHitDto(APP, ENDPOINT, IP, null);

        final Set<String> fieldsWithViolation = validate(dto);

        assertThat(fieldsWithViolation, contains("timestamp"));
    }

    @Test
    void whenTimestampInFuture_ThenValidationErrorForTimestamp() {
        final NewHitDto dto = new NewHitDto(APP, ENDPOINT, IP,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(2L));

        final Set<String> fieldsWithViolation = validate(dto);

        assertThat(fieldsWithViolation, contains("timestamp"));
    }

    @Test
    void whenTimestampNow_ThenNoValidationErrorForTimestamp() {
        final NewHitDto dto = new NewHitDto(APP, ENDPOINT, IP,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        final Set<String> fieldsWithViolations = validate(dto);

        assertThat(fieldsWithViolations, empty());
    }
}