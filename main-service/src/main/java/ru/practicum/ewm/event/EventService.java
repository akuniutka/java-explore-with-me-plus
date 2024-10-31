package ru.practicum.ewm.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface EventService {

    Event add(@NotNull @Valid Event event);
}
