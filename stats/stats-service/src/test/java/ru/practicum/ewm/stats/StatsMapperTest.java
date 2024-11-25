package ru.practicum.ewm.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static ru.practicum.ewm.stats.TestModels.makeTestViewStats;
import static ru.practicum.ewm.stats.TestModels.makeTestViewStatsDto;

class StatsMapperTest {

    private StatsMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new StatsMapper();
    }

    @Test
    void whenMapToDtoAndViewStatsIsNotNull_ThenReturnSingleDto() {

        final ViewStatsDto dto = mapper.mapToDto(makeTestViewStats());

        assertThat(dto, equalTo(makeTestViewStatsDto()));
    }

    @Test
    void whenMapToDtoAndViewStatsIsNull_ThenReturnNull() {

        final ViewStatsDto dto = mapper.mapToDto((ViewStats) null);

        assertThat(dto, nullValue());
    }

    @Test
    void whenMapToDtoAndViewStatsListIsNotNull_ThenReturnDtoList() {

        final List<ViewStatsDto> dtos = mapper.mapToDto(List.of(makeTestViewStats()));

        assertThat(dtos, contains(makeTestViewStatsDto()));
    }

    @Test
    void whenMapToDtoAndViewStatsListIsNull_ThenReturnNull() {

        final List<ViewStatsDto> dtos = mapper.mapToDto((List<ViewStats>) null);

        assertThat(dtos, nullValue());
    }
}