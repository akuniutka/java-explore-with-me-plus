package ru.practicum.ewm.request;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RequestMapper {

    RequestDto mapToRequestDto(Request request) {
        return RequestDto.builder()
                .requester(request.getRequester().getId())
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .status(request.getStatus())
                .build();
    }
}
