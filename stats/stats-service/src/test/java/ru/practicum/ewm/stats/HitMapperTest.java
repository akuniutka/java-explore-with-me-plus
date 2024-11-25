package ru.practicum.ewm.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static ru.practicum.ewm.stats.TestModels.NO_ID;
import static ru.practicum.ewm.stats.TestModels.makeTestHit;
import static ru.practicum.ewm.stats.TestModels.makeTestHitDto;
import static ru.practicum.ewm.stats.TestModels.makeTestNewHitDto;

class HitMapperTest {

    private HitMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new HitMapper();
    }

    @Test
    void whenMapToHitAndNewHitDtoIsNotNull_ThenReturnHit() {

        final Hit hit = mapper.mapToHit(makeTestNewHitDto());

        assertThat(hit, samePropertyValuesAs(makeTestHit(NO_ID)));
    }

    @Test
    void whenMapToHitAndNewHitDtoIsNull_ThenReturnNull() {

        final Hit hit = mapper.mapToHit(null);

        assertThat(hit, nullValue());
    }

    @Test
    void whenMapToDtoAndHitIsNotNull_ThenReturnSingleDto() {

        final HitDto dto = mapper.mapToDto(makeTestHit());

        assertThat(dto, equalTo(makeTestHitDto()));
    }

    @Test
    void whenMapToDtoAndHitIsNull_ThenReturnNull() {

        final HitDto dto = mapper.mapToDto((Hit) null);

        assertThat(dto, nullValue());
    }

    @Test
    void whenMapToDtoAndHitListIsNotNull_ThenReturnDtoList() {

        final List<HitDto> dtos = mapper.mapToDto(List.of(makeTestHit()));

        assertThat(dtos, contains(makeTestHitDto()));
    }

    @Test
    void whenMapToDtoAndHitListIsNull_ThenReturnNull() {

        final List<HitDto> dtos = mapper.mapToDto((List<Hit>) null);

        assertThat(dtos, nullValue());
    }
}