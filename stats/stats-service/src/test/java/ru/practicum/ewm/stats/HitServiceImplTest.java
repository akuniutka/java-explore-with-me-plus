package ru.practicum.ewm.stats;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.practicum.ewm.common.LogListener;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static ru.practicum.ewm.common.TestUtils.assertLogs;
import static ru.practicum.ewm.stats.TestModels.END;
import static ru.practicum.ewm.stats.TestModels.ENDPOINT;
import static ru.practicum.ewm.stats.TestModels.NO_ID;
import static ru.practicum.ewm.stats.TestModels.START;
import static ru.practicum.ewm.stats.TestModels.makeTestHit;
import static ru.practicum.ewm.stats.TestModels.makeTestViewStats;

class HitServiceImplTest {

    private static final LogListener logListener = new LogListener(HitServiceImpl.class);

    private AutoCloseable openMocks;

    @Mock
    private HitRepository repository;

    private HitService service;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        service = new HitServiceImpl(repository);
        logListener.startListen();
        logListener.reset();
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(repository);
        openMocks.close();
    }

    @Test
    void whenAddNewHit_ThenPassItToRepositoryAndReturnRepositoryResponseAndLog() throws Exception {
        when(repository.save(any())).thenReturn(makeTestHit());

        final Hit hit = service.addHit(makeTestHit(NO_ID));

        verify(repository).save(argThat(samePropertyValuesAs(makeTestHit(NO_ID))));
        assertThat(hit, samePropertyValuesAs(makeTestHit()));
        assertLogs(logListener.getEvents(), "add_hit.json", getClass());
    }

    @Test
    void whenGetViewStatsAndUrisAreNullAndUniqueIsFalse_ThenRetrieveHitCountForAllEndpointsAndReturnThem() {
        when(repository.getHits(any(), any())).thenReturn(List.of(makeTestViewStats()));

        final List<ViewStats> viewStats = service.getViewStats(START, END, null, false);

        verify(repository).getHits(START, END);
        assertThat(viewStats, contains(makeTestViewStats()));
    }

    @Test
    void whenGetViewStatsAndUrisAreNullAndUniqueIsTrue_ThenRetrieveUniqueHitCountForAllEndpointsAndReturnThem() {
        when(repository.getUniqueHits(any(), any())).thenReturn(List.of(makeTestViewStats()));

        final List<ViewStats> viewStats = service.getViewStats(START, END, null, true);

        verify(repository).getUniqueHits(START, END);
        assertThat(viewStats, contains(makeTestViewStats()));
    }

    @Test
    void whenGetViewStatsAndUrisAreEmptyAndUniqueIsFalse_ThenRetrieveHitCountsForAllEndpointsAndReturnThem() {
        when(repository.getHits(any(), any())).thenReturn(List.of(makeTestViewStats()));

        final List<ViewStats> viewStats = service.getViewStats(START, END, List.of(), false);

        verify(repository).getHits(START, END);
        assertThat(viewStats, contains(makeTestViewStats()));
    }

    @Test
    void whenGetViewStatsAndUrisAreEmptyAndUniqueIsTrue_ThenRetrieveUniqueHitCountForAllEndpointsAndReturnThem() {
        when(repository.getUniqueHits(any(), any())).thenReturn(List.of(makeTestViewStats()));

        final List<ViewStats> viewStats = service.getViewStats(START, END, List.of(), true);

        verify(repository).getUniqueHits(START, END);
        assertThat(viewStats, contains(makeTestViewStats()));
    }

    @Test
    void whenGetViewStatsAndUrisAreNotEmptyAndUniqueIsFalse_ThenRetrieveHitCountForChosenEndpointsAndReturnThem() {
        when(repository.getHits(any(), any(), anyList())).thenReturn(List.of(makeTestViewStats()));

        final List<ViewStats> viewStats = service.getViewStats(START, END, List.of(ENDPOINT), false);

        verify(repository).getHits(START, END, List.of(ENDPOINT));
        assertThat(viewStats, contains(makeTestViewStats()));
    }

    @Test
    void whenGetViewStatsAndUrisAreNotEmptyAndUniqueIsTrue_ThenRetrieveUniqueHitCountForChosenEndpointsAndReturnThem() {
        when(repository.getUniqueHits(any(), any(), anyList())).thenReturn(List.of(makeTestViewStats()));

        final List<ViewStats> viewStats = service.getViewStats(START, END, List.of(ENDPOINT), true);

        verify(repository).getUniqueHits(START, END, List.of(ENDPOINT));
        assertThat(viewStats, contains(makeTestViewStats()));
    }
}
