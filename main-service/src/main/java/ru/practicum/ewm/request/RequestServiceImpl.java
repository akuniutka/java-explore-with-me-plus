package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.NotPossibleException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventService eventService;
    private final UserService userService;

    @Override
    public List<RequestStats> getConfirmedRequestStats(final List<Long> eventIds) {
        List<RequestStats> stats = new ArrayList<>();
        RequestStats requestStats = new RequestStats();
        for (Long eventId : eventIds) {
            List<Request> requests = requestRepository.findAllByEventIdAndStatus(eventId, RequestState.CONFIRM);
            requestStats.setEventId(eventId);
            requestStats.setRequestCount(requests.size());
            stats.add(requestStats);
        }
        return stats;
    }

    @Override
    public RequestDto create(long userId, long eventId) {
        if (!requestRepository.findAllByUserIdAndEventId(userId, eventId).isEmpty())
            throw new NotPossibleException("Request already exists");
        User user = userService.getById(userId);
        Event event = eventService.getById(eventId);
        if (userId == event.getInitiator().getId())
            throw new NotPossibleException("User is Initiator of event");
        if (!event.getState().equals(EventState.PUBLISHED))
            throw new NotPossibleException("Event is not published");
        if (event.getConfirmedRequests() >= event.getParticipantLimit())
            throw new NotPossibleException("Request limit exceeded");
        Request newRequest = new Request();
        newRequest.setRequester(user);
        newRequest.setEvent(event);
        if (event.isRequestModeration()) {
            newRequest.setStatus(RequestState.PENDING);
        } else {
            newRequest.setStatus(RequestState.CONFIRM);
        }
        return RequestMapper.mapToRequestDto(requestRepository.save(newRequest));
    }

    @Override
    public List<RequestDto> getAllRequestByUserId(final long userId) {
        userService.getById(userId);
        return requestRepository.findAllByUserId(userId).stream()
                .map(RequestMapper::mapToRequestDto)
                .toList();
    }

    @Override
    public RequestDto delete(final long userId, final long requestId) {
        userService.getById(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(Request.class, requestId));
        requestRepository.deleteById(requestId);
        return RequestMapper.mapToRequestDto(request);
    }
}
