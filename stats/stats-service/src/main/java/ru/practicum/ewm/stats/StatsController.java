package ru.practicum.ewm.stats;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.HttpRequestResponseLogger;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
class StatsController extends HttpRequestResponseLogger {

    private final HitService service;
    private final StatsMapper mapper;

    @GetMapping
    List<ViewStatsDto> getViewStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final LocalDateTime end,
            @RequestParam(required = false) final List<String> uris,
            @RequestParam(defaultValue = "false") final boolean unique,
            final HttpServletRequest httpRequest
    ) {
        logHttpRequest(httpRequest);
        final List<ViewStatsDto> dtos = mapper.mapToDto(service.getViewStats(start, end, uris, unique));
        logHttpResponse(httpRequest, dtos);
        return dtos;
    }
}
