package ru.practicum.ewm.request;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.HttpRequestResponseLogger;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class RequestController extends HttpRequestResponseLogger {
    private final RequestService requestService;

    @GetMapping("/{userId}/requests")
    Collection<RequestDto> get(@PathVariable final long userId, final HttpServletRequest httpRequest) {
        Collection<RequestDto> response = requestService.getAllRequestByUserId(userId);
        logHttpResponse(httpRequest, response);
        return response;
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    RequestDto save(@PathVariable final long userId, @RequestParam int eventId, final HttpServletRequest httpRequest) {
        final RequestDto requestDto = requestService.create(userId, eventId);
        logHttpResponse(httpRequest, requestDto);
        return requestDto;
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    RequestDto delete(@PathVariable final long userId, @PathVariable int requestId, final HttpServletRequest request) {
        logHttpRequest(request);
        RequestDto requestDto = requestService.delete(userId, requestId);
        logHttpResponse(request, requestDto);
        return requestDto;
    }
}