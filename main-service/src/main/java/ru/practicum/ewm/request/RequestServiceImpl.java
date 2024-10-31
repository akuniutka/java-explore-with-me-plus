package ru.practicum.ewm.request;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
class RequestServiceImpl implements RequestService {

    @Override
    public List<RequestStats> getConfirmedRequestStats(final List<Long> eventIds) {
        return List.of();
    }
}
