package ru.practicum.ewm.stats;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.common.TestUtils.loadJson;
import static ru.practicum.ewm.stats.TestModels.END;
import static ru.practicum.ewm.stats.TestModels.ENDPOINT;
import static ru.practicum.ewm.stats.TestModels.START;
import static ru.practicum.ewm.stats.TestModels.makeTestViewStats;
import static ru.practicum.ewm.stats.TestModels.makeTestViewStatsDto;

@WebMvcTest(controllers = StatsController.class)
@ContextConfiguration(classes = ClockConfig.class)
class StatsControllerIT {

    private static final String BASE_PATH = "/stats";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String START_VALUE = START.format(FORMATTER);
    private static final String END_VALUE = END.format(FORMATTER);
    private static final String FALSE = "false";

    @MockBean
    private HitService mockService;

    @MockBean
    private StatsMapper mockMapper;

    private InOrder inOrder;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        Mockito.reset(mockService, mockMapper);
        inOrder = Mockito.inOrder(mockService, mockMapper);
    }

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(mockService, mockMapper);
    }

    @Test
    void whenGetAtBasePath_ThenInvokeGetViewStatsMethodAndProcessResponse() throws Exception {
        final String responseBody = loadJson("get_view_stats_response.json", getClass());
        when(mockService.getViewStats(any(), any(), anyList(), anyBoolean())).thenReturn(List.of(makeTestViewStats()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestViewStatsDto()));

        mvc.perform(get(BASE_PATH)
                        .param("start", START_VALUE)
                        .param("end", END_VALUE)
                        .param("uris", ENDPOINT)
                        .param("unique", FALSE)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).getViewStats(START, END, List.of(ENDPOINT), false);
        inOrder.verify(mockMapper).mapToDto(List.of(makeTestViewStats()));
    }

    @Test
    void whenGetAtBasePathAndNoStartDate_ThenRespondWithBadRequestError() throws Exception {
        final String responseBody = loadJson("get_view_stats_no_start_response.json", getClass());

        mvc.perform(get(BASE_PATH)
                        .param("end", END_VALUE)
                        .param("uris", ENDPOINT)
                        .param("unique", FALSE)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));
    }

    @Test
    void whenGetAtBasePathAndNoEndDate_ThenRespondWithBadRequestError() throws Exception {
        final String responseBody = loadJson("get_view_stats_no_end_response.json", getClass());

        mvc.perform(get(BASE_PATH)
                        .param("start", START_VALUE)
                        .param("uris", ENDPOINT)
                        .param("unique", FALSE)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));
    }

    @Test
    void whenGetAtBasePathAndUrisAreEmpty_ThenInvokeGetViewStatsMethodAndProcessResponse() throws Exception {
        final String responseBody = loadJson("get_view_stats_empty_uris_response.json", getClass());
        when(mockService.getViewStats(any(), any(), anyList(), anyBoolean())).thenReturn(List.of(makeTestViewStats()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestViewStatsDto()));

        mvc.perform(get(BASE_PATH)
                        .param("start", START_VALUE)
                        .param("end", END_VALUE)
                        .param("uris", "")
                        .param("unique", FALSE)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).getViewStats(START, END, List.of(), false);
        inOrder.verify(mockMapper).mapToDto(List.of(makeTestViewStats()));
    }

    @Test
    void whenGetAtBasePathAndNoUris_ThenInvokeGetViewStatsMethodAndProcessResponse() throws Exception {
        final String responseBody = loadJson("get_view_stats_no_uris_response.json", getClass());
        when(mockService.getViewStats(any(), any(), any(), anyBoolean())).thenReturn(List.of(makeTestViewStats()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestViewStatsDto()));

        mvc.perform(get(BASE_PATH)
                        .param("start", START_VALUE)
                        .param("end", END_VALUE)
                        .param("unique", FALSE)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).getViewStats(START, END, null, false);
        inOrder.verify(mockMapper).mapToDto(List.of(makeTestViewStats()));
    }

    @Test
    void whenGetAtBasePathAndNoUnique_ThenInvokeGetViewStatsMethodWithUniqueFalseAndProcessResponse() throws Exception {
        final String responseBody = loadJson("get_view_stats_no_unique_response.json", getClass());
        when(mockService.getViewStats(any(), any(), anyList(), anyBoolean())).thenReturn(List.of(makeTestViewStats()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestViewStatsDto()));

        mvc.perform(get(BASE_PATH)
                        .param("start", START_VALUE)
                        .param("end", END_VALUE)
                        .param("uris", ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).getViewStats(START, END, List.of(ENDPOINT), false);
        inOrder.verify(mockMapper).mapToDto(List.of(makeTestViewStats()));
    }
}