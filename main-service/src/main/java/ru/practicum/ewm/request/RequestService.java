package ru.practicum.ewm.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface RequestService {

    List<RequestStats> getConfirmedRequestStats(@NotNull List<Long> eventIds);

    RequestDto create(long userId, long eventId);

    List<RequestDto> getAllRequestByUserId(long userId);

    RequestDto delete(long userId, long requestId);
}
