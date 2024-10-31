package ru.practicum.ewm.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface RequestService {

    List<RequestStats> getConfirmedRequestStats(@NotNull List<Long> eventIds);
}
