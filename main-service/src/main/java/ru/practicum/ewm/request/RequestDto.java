package ru.practicum.ewm.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
record RequestDto(

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime created,

        long event,

        long id,

        long requester,

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        RequestState status) {
}