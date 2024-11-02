package ru.practicum.ewm.compilation;

import lombok.Builder;
import ru.practicum.ewm.event.EventShortDto;

import java.util.List;

@Builder
public record CompilationDto(
        List<EventShortDto> events,
        Long id,
        Boolean pinned,
        String title) {
}
