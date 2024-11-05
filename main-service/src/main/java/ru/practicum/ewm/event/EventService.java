package ru.practicum.ewm.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface EventService {

    Event add(@NotNull @Valid Event event);

    Event getById(long id);

    Event getByIdAndUserId(long id, long userId);

    List<Event> get(@NotNull @Valid EventFilter filter);

    List<Event> getByIds(List<Long> ids);

    Event update(long id, @NotNull @Valid EventPatch patch);

    Event update(long id, @NotNull @Valid EventPatch patch, long userId);
}
