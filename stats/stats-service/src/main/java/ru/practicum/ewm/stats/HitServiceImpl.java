package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import ru.practicum.ewm.exception.ParameterValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
class HitServiceImpl implements HitService {

    private final HitRepository repository;

    @Override
    public Hit addHit(final Hit hit) {
        final Hit savedHit = repository.save(hit);
        log.info("Added hit with id = {}: {}", savedHit.getId(), savedHit);
        return savedHit;
    }

    @Override
    public List<ViewStats> getViewStats(final LocalDateTime start, final LocalDateTime end, final List<String> uris,
            final boolean unique) {
        if (end.isBefore(start)) {
            throw new ParameterValidationException("end", "must be after or equal to 'start'", end);
        }
        if (CollectionUtils.isEmpty(uris)) {
            if (unique) {
                return repository.getUniqueHits(start, end);
            } else {
                return repository.getHits(start, end);
            }
        } else {
            if (unique) {
                return repository.getUniqueHits(start, end, uris);
            } else {
                return repository.getHits(start, end, uris);
            }
        }
    }
}
