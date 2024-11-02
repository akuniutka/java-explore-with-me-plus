package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.HttpRequestResponseLogger;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
class EventAdminController extends HttpRequestResponseLogger {

    private final EventService events;
    private final EventMapper mapper;

    @PatchMapping("/{eventId}")
    EventFullDto update(
            @PathVariable final long eventId,
            @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest, updateEventAdminRequest);
        final EventPatch patch = mapper.mapToPatch(updateEventAdminRequest);
        final EventFullDto dto = mapper.mapToFullDto(events.update(eventId, patch));
        logHttpResponse(httpRequest, dto);
        return dto;
    }

}
