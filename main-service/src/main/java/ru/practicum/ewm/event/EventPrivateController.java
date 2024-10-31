package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.HttpRequestResponseLogger;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
class EventPrivateController extends HttpRequestResponseLogger {

    private final EventService events;
    private final EventMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto add(
            @PathVariable final long userId,
            @RequestBody @Valid final NewEventDto newEventDto,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest, newEventDto);
        final Event event = mapper.mapToEvent(userId, newEventDto);
        final EventFullDto dto = mapper.mapToFullDto(events.add(event));
        logHttpResponse(httpRequest, dto);
        return dto;
    }

    @GetMapping("{eventId}")
    EventFullDto get(
            @PathVariable final long userId,
            @PathVariable final long eventId,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        final EventFullDto dto = mapper.mapToFullDto(events.getById(eventId, userId));
        logHttpResponse(httpRequest, dto);
        return dto;
    }

    @PatchMapping("/{eventId}")
    EventFullDto update(
            @PathVariable final long userId,
            @PathVariable final long eventId,
            @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest, updateEventUserRequest);
        final EventPatch patch = mapper.mapToPatch(updateEventUserRequest);
        final EventFullDto dto = mapper.mapToFullDto(events.update(eventId, patch, userId));
        logHttpResponse(httpRequest, dto);
        return dto;
    }
}
