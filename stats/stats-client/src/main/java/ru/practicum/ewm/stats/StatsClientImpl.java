package ru.practicum.ewm.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class StatsClientImpl implements StatsClient {

    private static final String HIT_URI = "/hit";
    private static final String STATS_URI = "/stats";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RestClient restClient;

    public StatsClientImpl(final RestClient.Builder builder, @Value("${stats.server.uri}") final String uri) {
        this.restClient = builder
                .baseUrl(uri)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Optional<HitDto> saveHit(final NewHitDto newHitDto) {
        Objects.requireNonNull(newHitDto, "Cannot send new hit data to stats server: is null");
        try {
            log.debug("Sending new hit data to stats service: {}", newHitDto);
            final HitDto hitDto = restClient.post()
                    .uri(HIT_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(newHitDto)
                    .retrieve()
                    .body(HitDto.class);
            log.debug("Received response from stats service: {}", hitDto);
            return Optional.ofNullable(hitDto);
        } catch (RestClientException e) {
            log.error("Error while sending new hit data to stats service: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public List<ViewStatsDto> getStats(final LocalDateTime start, final LocalDateTime end, final List<String> uris,
            final boolean unique) {
        try {
            log.debug("Requesting hits data from stats service: start={}, end={}, uris={}, unique={}", start, end,
                    uris, unique);
            final List<ViewStatsDto> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(STATS_URI)
                            .queryParam("start", start.format(FORMATTER))
                            .queryParam("end", end.format(FORMATTER))
                            .queryParam("uris", uris)
                            .queryParam("unique", unique)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            log.debug("Received hits data from stats service: {}", response);
            return response;
        } catch (RestClientException e) {
            log.error("Error while retrieving hits data from stats service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
