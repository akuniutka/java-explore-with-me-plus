package ru.practicum.ewm.stats;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
class HitMapper {

    Hit mapToHit(final NewHitDto dto) {
        if (dto == null) {
            return null;
        }
        final Hit hit = new Hit();
        hit.setApp(dto.app());
        hit.setUri(dto.uri());
        hit.setIp(dto.ip());
        hit.setTimestamp(dto.timestamp());
        return hit;
    }

    HitDto mapToDto(final Hit hit) {
        if (hit == null) {
            return null;
        }
        return HitDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp())
                .build();
    }

    List<HitDto> mapToDto(final List<Hit> hits) {
        if (hits == null) {
            return null;
        }
        return hits.stream()
                .map(this::mapToDto)
                .toList();
    }
}
