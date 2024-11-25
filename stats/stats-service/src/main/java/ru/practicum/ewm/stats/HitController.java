package ru.practicum.ewm.stats;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.HttpRequestResponseLogger;

@RestController
@RequestMapping("/hit")
@RequiredArgsConstructor
class HitController extends HttpRequestResponseLogger {

    private final HitService service;
    private final HitMapper mapper;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    HitDto add(@RequestBody @Valid final NewHitDto newHitDto, final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest, newHitDto);
        final Hit hit = service.addHit(mapper.mapToHit(newHitDto));
        final HitDto dto = mapper.mapToDto(hit);
        logHttpResponse(httpRequest, dto);
        return dto;
    }
}
