package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByUserIdAndEventId(long userId, long eventId);

    List<Request> findAllByUserId(long userId);

    List<Request> findAllByEventIdAndStatus(Long event_id, RequestState status);
}
