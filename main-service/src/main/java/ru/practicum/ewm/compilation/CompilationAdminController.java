package ru.practicum.ewm.compilation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.HttpRequestResponseLogger;

@RestController
@RequestMapping("admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController extends HttpRequestResponseLogger {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto save(@RequestBody @Valid NewCompilationDto requestDto, final HttpServletRequest request) {
        logHttpRequest(request);
        final CompilationDto responseDto = compilationService.save(requestDto);
        logHttpResponse(request, responseDto);
        return responseDto;
    }
}
