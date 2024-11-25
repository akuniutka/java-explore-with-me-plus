package ru.practicum.ewm.stats;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.ewm.common.AbstractControllerTest;
import ru.practicum.ewm.common.LogListener;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static ru.practicum.ewm.common.TestUtils.assertLogs;
import static ru.practicum.ewm.stats.TestModels.END;
import static ru.practicum.ewm.stats.TestModels.ENDPOINT;
import static ru.practicum.ewm.stats.TestModels.START;
import static ru.practicum.ewm.stats.TestModels.makeTestViewStats;
import static ru.practicum.ewm.stats.TestModels.makeTestViewStatsDto;

class StatsControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(StatsController.class);

    @Mock
    private HitService mockService;

    @Mock
    private StatsMapper mockMapper;

    private StatsController controller;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        inOrder = Mockito.inOrder(mockService, mockMapper);
        logListener.startListen();
        logListener.reset();
        controller = new StatsController(mockService, mockMapper);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockService, mockMapper);
        super.tearDown();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void whenGetViewStatsWithUris_ThenPassParamsToServiceAndMapResponseToDtosAndReturnItAndLog(final boolean unique)
            throws Exception {
        when(mockService.getViewStats(any(), any(), anyList(), anyBoolean())).thenReturn(List.of(makeTestViewStats()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestViewStatsDto()));

        final List<ViewStatsDto> dtos = controller.getViewStats(START, END, List.of(ENDPOINT), unique, mockHttpRequest);

        inOrder.verify(mockService).getViewStats(START, END, List.of(ENDPOINT), unique);
        inOrder.verify(mockMapper).mapToDto(List.of(makeTestViewStats()));
        assertThat(dtos, contains(makeTestViewStatsDto()));
        assertLogs(logListener.getEvents(), "get_view_stats_with_uris.json", getClass());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void whenGetViewStatsWithNullUris_ThenPassParamsToServiceAndMapResponseToDtosAndReturnItAndLog(final boolean unique)
            throws Exception {
        when(mockService.getViewStats(any(), any(), any(), anyBoolean())).thenReturn(List.of(makeTestViewStats()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestViewStatsDto()));

        final List<ViewStatsDto> dtos = controller.getViewStats(START, END, null, unique, mockHttpRequest);

        inOrder.verify(mockService).getViewStats(START, END, null, unique);
        inOrder.verify(mockMapper).mapToDto(List.of(makeTestViewStats()));
        assertThat(dtos, contains(makeTestViewStatsDto()));
        assertLogs(logListener.getEvents(), "get_view_stats_with_null_uris.json", getClass());
    }
}