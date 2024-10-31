package ru.practicum.ewm.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface EventService {

    Event add(@NotNull @Valid Event event);

    Event getById(long id);

    Event getById(long id, long userId);

    Event update(long id, @NotNull @Valid EventPatch patch);

    Event update(long id, @NotNull @Valid EventPatch patch, long userId);
}
