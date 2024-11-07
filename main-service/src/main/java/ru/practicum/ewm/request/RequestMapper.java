package ru.practicum.ewm.request;

public class RequestMapper {

    private RequestMapper() {
    }

    static RequestDto mapToRequestDto(Request request) {
        return RequestDto.builder()
                .requester(request.getRequester().getId())
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .status(request.getStatus())
                .build();
    }
}
