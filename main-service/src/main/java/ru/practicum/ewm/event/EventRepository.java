package ru.practicum.ewm.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e from Event e join fetch e.initiator join fetch e.category where e.id = :id")
    Optional<Event> findByIdWithRelations(@Param("id") long id);

    List<Event> findByIdIn(List<Long> ids);
}
