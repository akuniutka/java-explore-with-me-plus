package ru.practicum.ewm.stats;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

final class TestModels {

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static final String SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 9090;
    static final String APP = "mainService";
    static final String ENDPOINT = "endpointA";
    static final String IP = "127.0.0.1";
    static final long HITS = 99L;
    static final LocalDateTime TIMESTAMP = LocalDateTime.of(2000, Month.JANUARY, 31, 13, 30, 55);
    static final LocalDateTime START = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 1);
    static final LocalDateTime END = LocalDateTime.of(2000, Month.FEBRUARY, 2, 0, 0, 2);

    private TestModels() {
    }

    static EndpointHitDto makeTestEndpointHitDto() {
        return EndpointHitDto.builder()
                .app(APP)
                .uri(ENDPOINT)
                .ip(IP)
                .timestamp(TIMESTAMP)
                .build();
    }

    static ViewStatsDto makeTestViewStatsDto() {
        return ViewStatsDto.builder()
                .app(APP)
                .uri(ENDPOINT)
                .hits(HITS)
                .build();
    }
}
