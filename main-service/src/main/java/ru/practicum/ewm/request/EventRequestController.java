package ru.practicum.ewm.request;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.HttpRequestResponseLogger;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@RequiredArgsConstructor
class EventRequestController extends HttpRequestResponseLogger {

    private final RequestService requestService;

    @GetMapping
    List<RequestDto> getRequests(
            @PathVariable final long userId,
            @PathVariable final long eventId,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        final List<RequestDto> dtos = requestService.getRequests(userId, eventId);
        logHttpResponse(httpRequest, dtos);
        return dtos;
    }

    @PatchMapping
    EventRequestStatusDto processRequests(
            @PathVariable final long userId,
            @PathVariable final long eventId,
            @RequestBody @Valid final UpdateEventRequestStatusDto updateDto,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest, updateDto);
        final EventRequestStatusDto dto = requestService.processRequests(eventId, updateDto, userId);
        logHttpResponse(httpRequest, dto);
        return dto;
    }
}
