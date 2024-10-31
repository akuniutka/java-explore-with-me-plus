package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.HttpRequestResponseLogger;
import ru.practicum.ewm.stats.EndpointHitDto;
import ru.practicum.ewm.stats.StatsClient;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
class EventPublicController extends HttpRequestResponseLogger {

    private static final String APP = "main-service";

    private final EventService events;
    private final EventMapper mapper;
    private final StatsClient statsClient;
    private final Clock clock;

    @GetMapping("{eventId}")
    EventFullDto get(
            @PathVariable final long eventId,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        final EventFullDto dto = mapper.mapToFullDto(events.getById(eventId));
        statsClient.saveHit(new EndpointHitDto(APP, httpRequest.getRequestURI(), httpRequest.getRemoteAddr(),
                LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS)));
        logHttpResponse(httpRequest, dto);
        return dto;
    }

}
