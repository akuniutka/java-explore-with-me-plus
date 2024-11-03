package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.exception.FieldValidationException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.NotPossibleException;
import ru.practicum.ewm.request.RequestService;
import ru.practicum.ewm.request.RequestStats;
import ru.practicum.ewm.stats.StatsClient;
import ru.practicum.ewm.stats.ViewStatsDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserService;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
class EventServiceImpl implements EventService {

    private static final LocalDateTime VIEWS_FROM = LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0, 0);
    private static final LocalDateTime VIEWS_TO = LocalDateTime.of(2100, Month.DECEMBER, 31, 23, 59, 59);
    private static final Duration ADMIN_TIME_LIMIT = Duration.ofHours(1L);
    private static final Duration USER_TIME_LIMIT = Duration.ofHours(2L);

    private final Clock clock;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatsClient statsClient;
    private final EventRepository repository;
    private RequestService requestService;

    @Autowired
    public void setRequestService(final RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    @Transactional
    public Event add(final Event event) {
        validateEventDate(event.getEventDate(), USER_TIME_LIMIT);
        event.setInitiator(fetchUser(event.getInitiator()));
        event.setCategory(fetchCategory(event.getCategory()));
        final Event savedEvent = repository.save(event);
        log.info("Added event with id = {}: {}", savedEvent.getId(), savedEvent);
        return savedEvent;
    }

    @Override
    public Event getById(long id) {
        final Event event = getByIdInternally(id);
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException(Event.class, id);
        }
        return event;
    }

    @Override
    public Event getById(final long id, final long userId) {
        final Event event = getByIdInternally(id);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NotFoundException(Event.class, id);
        }
        return event;
    }

    @Override
    public List<Event> getByIds(final List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return repository.findByIdIn(ids);
    }

    @Override
    @Transactional
    public Event update(final long id, final EventPatch patch) {
        validateEventDate(patch.eventDate(), ADMIN_TIME_LIMIT);
        final Event event = getByIdInternally(id);
        if (event.getState() != EventState.PENDING) {
            throw new NotPossibleException("Event must be in state PENDING");
        }
        if (patch.eventDate() == null && isFreezeTime(event.getEventDate(), ADMIN_TIME_LIMIT)) {
            throw new NotPossibleException("Event date must be not earlier than in %s from now"
                    .formatted(USER_TIME_LIMIT));
        }
        applyPatch(event, patch);
        if (event.getState() == EventState.PUBLISHED) {
            event.setPublishedOn(now());
        }
        repository.save(event);
        return event;
    }

    @Override
    @Transactional
    public Event update(final long id, final EventPatch patch, final long userId) {
        validateEventDate(patch.eventDate(), USER_TIME_LIMIT);
        final Event event = getById(id, userId);
        if (event.getState() == EventState.PUBLISHED) {
            throw new NotPossibleException("Only pending or canceled events can be changed");
        }
        if (patch.eventDate() == null && isFreezeTime(event.getEventDate(), USER_TIME_LIMIT)) {
            throw new NotPossibleException("Event date must be not earlier than in %s from now"
                    .formatted(USER_TIME_LIMIT));
        }
        applyPatch(event, patch);
        repository.save(event);
        return event;
    }

    private void validateEventDate(final LocalDateTime eventDate, final Duration timeLimit) {
        if (isFreezeTime(eventDate, timeLimit)) {
            throw new FieldValidationException("eventDate",
                    "must be not earlier than in %s from now".formatted(timeLimit), eventDate);
        }
    }

    private boolean isFreezeTime(final LocalDateTime dateTime, final Duration timeLimit) {
        return dateTime != null && !Duration.between(now(), dateTime.minus(timeLimit)).isPositive();
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
    }

    private Event getByIdInternally(final long id) {
        final Event event = repository.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException(Event.class, id));
        final Map<Long, Long> confirmedRequests = requestService.getConfirmedRequestStats(List.of(id)).stream()
                .collect(Collectors.toMap(RequestStats::getEventId, RequestStats::getRequestCount));
        event.setConfirmedRequests(confirmedRequests.getOrDefault(id, 0L));
        final String endpoint = "/events/%d".formatted(id);
        final Map<String, Long> views = statsClient.getStats(VIEWS_FROM, VIEWS_TO, List.of(endpoint), true).stream()
                .collect(Collectors.toMap(ViewStatsDto::uri, ViewStatsDto::hits));
        event.setViews(views.getOrDefault(endpoint, 0L));
        return event;
    }

    private void applyPatch(final Event event, final EventPatch patch) {
        Optional.ofNullable(patch.title()).ifPresent(event::setTitle);
        Optional.ofNullable(patch.category()).map(this::fetchCategory).ifPresent(event::setCategory);
        Optional.ofNullable(patch.eventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(patch.location()).ifPresent(event::setLocation);
        Optional.ofNullable(patch.annotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(patch.description()).ifPresent(event::setDescription);
        Optional.ofNullable(patch.participantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(patch.paid()).ifPresent(event::setPaid);
        Optional.ofNullable(patch.requestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(patch.state()).ifPresent(event::setState);
    }

    private User fetchUser(final User user) {
        if (user == null || user.getId() == null) {
            throw new AssertionError();
        }
        return userService.getById(user.getId());
    }

    private Category fetchCategory(final Category category) {
        if (category == null || category.getId() == null) {
            throw new AssertionError();
        }
        return categoryService.getById(category.getId());
    }
}
