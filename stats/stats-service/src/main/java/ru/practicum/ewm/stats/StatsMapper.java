package ru.practicum.ewm.stats;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
class StatsMapper {

    ViewStatsDto mapToDto(final ViewStats viewStats) {
        if (viewStats == null) {
            return null;
        }
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }

    List<ViewStatsDto> mapToDto(final List<ViewStats> viewStats) {
        if (viewStats == null) {
            return null;
        }
        return viewStats.stream()
                .map(this::mapToDto)
                .toList();
    }
}
