package ru.practicum.ewm.stats;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {

    Hit addHit(@NotNull Hit hit);

    List<ViewStats> getViewStats(@NotNull LocalDateTime start, @NotNull LocalDateTime end, List<String> uris,
            boolean unique);
}
