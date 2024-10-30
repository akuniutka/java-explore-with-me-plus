package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.HttpRequestResponseLogger;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class EventPrivateController extends HttpRequestResponseLogger {

    private final EventService events;
    private final EventMapper mapper;

    @PostMapping("/{id}/events")
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto add(
            @PathVariable final long id,
            @RequestBody @Valid final NewEventDto newEventDto,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest, newEventDto);
        final Event event = mapper.mapToEvent(id, newEventDto);
        final EventFullDto dto = mapper.mapToFullDto(events.add(event));
        logHttpResponse(httpRequest, dto);
        return dto;
    }
}
