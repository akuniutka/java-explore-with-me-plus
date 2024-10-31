package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.user.UserMapper;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final Clock clock;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    Event mapToEvent(final Long userId, final NewEventDto dto) {
        if (userId == null && dto == null) {
            return null;
        }
        final Event event = new Event();
        event.setInitiator(userMapper.mapToUser(userId));
        if (dto != null) {
            event.setTitle(dto.title());
            event.setCategory(categoryMapper.mapToCategory(dto.category()));
            event.setEventDate(dto.eventDate());
            event.setLocation(copyLocation(dto.location()));
            event.setAnnotation(dto.annotation());
            event.setDescription(dto.description());
            event.setParticipantLimit(dto.participantLimit() == null ? 0 : dto.participantLimit());
            event.setPaid(Boolean.TRUE.equals(dto.paid()));
            event.setRequestModeration(dto.requestModeration() == null || dto.requestModeration());
        }
        event.setCreatedOn(LocalDateTime.now(clock));
        event.setState(EventState.PENDING);
        return event;
    }

    EventFullDto mapToFullDto(final Event event) {
        if (event == null) {
            return null;
        }
        return EventFullDto.builder()
                .id(event.getId())
                .initiator(userMapper.mapToShortDto(event.getInitiator()))
                .title(event.getTitle())
                .category(categoryMapper.mapToDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .location(copyLocation(event.getLocation()))
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .paid(event.isPaid())
                .requestModeration(event.isRequestModeration())
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .build();
    }

    private Location copyLocation(final Location location) {
        final Location copy = new Location();
        copy.setLat(location.getLat());
        copy.setLon(location.getLon());
        return copy;
    }
}
