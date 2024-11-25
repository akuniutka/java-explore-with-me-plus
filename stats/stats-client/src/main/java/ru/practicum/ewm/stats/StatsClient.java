package ru.practicum.ewm.stats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StatsClient {

    Optional<HitDto> saveHit(NewHitDto newHitDto);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
