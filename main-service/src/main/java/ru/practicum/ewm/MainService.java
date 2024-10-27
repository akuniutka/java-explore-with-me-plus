package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.practicum.ewm.stats.EndpointHitDto;
import ru.practicum.ewm.stats.StatsClient;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
public class MainService {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MainService.class, args);
        StatsClient client = context.getBean(StatsClient.class);
        final LocalDateTime timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        client.saveHit(EndpointHitDto.builder()
                .app("mainService")
                .uri("endpointA")
                .ip("127.0.0.1")
                .timestamp(timestamp)
                .build()
        );
        client.saveHit(EndpointHitDto.builder()
                .app("mainService")
                .uri("endpointB")
                .ip("127.0.0.1")
                .timestamp(timestamp)
                .build()
        );
        client.saveHit(EndpointHitDto.builder()
                .app("mainService")
                .uri("endpointB")
                .ip("127.0.0.1")
                .timestamp(timestamp)
                .build()
        );
        client.getStats(timestamp.minusDays(1L), timestamp.plusDays(1L), null, false);
        client.getStats(timestamp.minusDays(1L), timestamp.plusDays(1L), List.of("endpointB"), false);
        client.getStats(timestamp.minusDays(1L), timestamp.plusDays(1L), List.of("endpointB"), true);
    }

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
