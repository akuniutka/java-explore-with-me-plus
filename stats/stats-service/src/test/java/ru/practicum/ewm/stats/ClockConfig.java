package ru.practicum.ewm.stats;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@TestConfiguration
public class ClockConfig {

    @Bean
    @Primary
    Clock fixedClock() {
        return Clock.fixed(Instant.parse("2000-01-01T00:00:01Z"), ZoneId.of("Z"));
    }
}
