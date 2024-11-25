package ru.practicum.ewm.stats;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.common.LogListener;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static ru.practicum.ewm.common.TestUtils.assertLogs;
import static ru.practicum.ewm.common.TestUtils.loadJson;
import static ru.practicum.ewm.stats.TestModels.END;
import static ru.practicum.ewm.stats.TestModels.ENDPOINT;
import static ru.practicum.ewm.stats.TestModels.FORMATTER;
import static ru.practicum.ewm.stats.TestModels.HOST;
import static ru.practicum.ewm.stats.TestModels.PORT;
import static ru.practicum.ewm.stats.TestModels.SCHEMA;
import static ru.practicum.ewm.stats.TestModels.START;
import static ru.practicum.ewm.stats.TestModels.makeTestHitDto;
import static ru.practicum.ewm.stats.TestModels.makeTestNewHitDto;
import static ru.practicum.ewm.stats.TestModels.makeTestViewStatsDto;

@RestClientTest
@ContextConfiguration(classes = StatsClientImpl.class)
class StatsClientImplIT {

    private static final LogListener logListener = new LogListener(StatsClientImpl.class);

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private StatsClient client;

    @BeforeEach
    void setUp() {
        mockServer.reset();
        logListener.startListen();
        logListener.reset();
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        mockServer.verify();
    }

    @Test
    void whenSaveHitAndNewHitDtoIsNull_ThenThrowException() {

        final NullPointerException exception = assertThrows(NullPointerException.class, () -> client.saveHit(null));

        assertThat(exception.getMessage(), is("Cannot send new hit data to stats server: is null"));
    }

    @Test
    void whenSaveHitAndStatsServiceRespondsWithHitDto_ThenReturnOptionalWithResponseAndLog() throws Exception {
        final String requestBody = loadJson("save_hit_request.json", getClass());
        final String responseBody = loadJson("save_hit_response.json", getClass());
        mockServer.expect(ExpectedCount.once(), requestTo(hitUri()))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody, true))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseBody));

        final Optional<HitDto> dto = client.saveHit(makeTestNewHitDto());

        assertThat(dto.isEmpty(), is(false));
        assertThat(dto.get(), equalTo(makeTestHitDto()));
        assertLogs(logListener.getEvents(), "logs/save_hit.json", getClass());
    }

    @Test
    void whenSaveHitAndStatsServiceRespondsWith4xxError_ThenReturnEmptyOptionalAndLog() throws Exception {
        final String requestBody = loadJson("save_hit_request.json", getClass());
        mockServer.expect(ExpectedCount.once(), requestTo(hitUri()))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody, true))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        final Optional<HitDto> dto = client.saveHit(makeTestNewHitDto());

        assertThat(dto.isEmpty(), is(true));
        assertLogs(logListener.getEvents(), "logs/save_hit_bad_request.json", getClass());
    }

    @Test
    void whenSaveHitAndStatsServiceRespondsWith5xxError_ThenReturnEmptyOptionalAndLog() throws Exception {
        final String requestBody = loadJson("save_hit_request.json", getClass());
        mockServer.expect(ExpectedCount.once(), requestTo(hitUri()))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody, true))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        final Optional<HitDto> dto = client.saveHit(makeTestNewHitDto());

        assertThat(dto.isEmpty(), is(true));
        assertLogs(logListener.getEvents(), "logs/save_hit_internal_server_error.json", getClass());
    }

    @Test
    void whenGetStatsAndUrisAreNotNull_ThenPassParamsToStatsServiceAndReturnResponseAndLog() throws Exception {
        final String responseBody = loadJson("get_stats.json", getClass());
        mockServer.expect(ExpectedCount.once(), requestTo(statsUri(List.of(ENDPOINT), false)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseBody));

        final List<ViewStatsDto> dtos = client.getStats(START, END, List.of(ENDPOINT), false);

        assertThat(dtos, contains(makeTestViewStatsDto()));
        assertLogs(logListener.getEvents(), "logs/get_stats.json", getClass());
    }

    @Test
    void whenGetStatsAndUrisAreNull_ThenPassParamsToStatsServiceAndReturnResponseAndLog() throws Exception {
        final String responseBody = loadJson("get_stats.json", getClass());
        mockServer.expect(ExpectedCount.once(), requestTo(statsUri(null, false)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseBody));

        final List<ViewStatsDto> dtos = client.getStats(START, END, null, false);

        assertThat(dtos, contains(makeTestViewStatsDto()));
        assertLogs(logListener.getEvents(), "logs/get_stats_uris_null.json", getClass());
    }

    @Test
    void whenGetStatsAndUrisAreEmpty_ThenPassParamsToStatsServiceAndReturnResponseAndLog() throws Exception {
        final String responseBody = loadJson("get_stats.json", getClass());
        mockServer.expect(ExpectedCount.once(), requestTo(statsUri(List.of(), false)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseBody));

        final List<ViewStatsDto> dtos = client.getStats(START, END, List.of(), false);

        assertThat(dtos, contains(makeTestViewStatsDto()));
        assertLogs(logListener.getEvents(), "logs/get_stats_uris_empty.json", getClass());
    }

    @Test
    void whenGetStatsAndUniqueIsTrue_ThenPassParamsToStatsServiceAndReturnResponseAndLog() throws Exception {
        final String responseBody = loadJson("get_stats.json", getClass());
        mockServer.expect(ExpectedCount.once(), requestTo(statsUri(List.of(ENDPOINT), true)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseBody));

        final List<ViewStatsDto> dtos = client.getStats(START, END, List.of(ENDPOINT), true);

        assertThat(dtos, contains(makeTestViewStatsDto()));
        assertLogs(logListener.getEvents(), "logs/get_stats_unique_true.json", getClass());
    }

    @Test
    void whenGetStatsAndStatsServiceRespondsWith4xxError_ThenReturnEmptyListAndLog() throws Exception {
        mockServer.expect(ExpectedCount.once(), requestTo(statsUri(List.of(ENDPOINT), false)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        final List<ViewStatsDto> dtos = client.getStats(START, END, List.of(ENDPOINT), false);

        assertThat(dtos, empty());
        assertLogs(logListener.getEvents(), "logs/get_stats_bad_request.json", getClass());
    }

    @Test
    void whenGetStatsAndStatsServiceRespondsWith5xxError_ThenReturnEmptyListAndLog() throws Exception {
        mockServer.expect(ExpectedCount.once(), requestTo(statsUri(List.of(ENDPOINT), false)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        final List<ViewStatsDto> dtos = client.getStats(START, END, List.of(ENDPOINT), false);

        assertThat(dtos, empty());
        assertLogs(logListener.getEvents(), "logs/get_stats_internal_server_error.json", getClass());
    }

    private String hitUri() {
        return UriComponentsBuilder.newInstance()
                .scheme(SCHEMA)
                .host(HOST)
                .port(PORT)
                .path("/hit")
                .build()
                .encode()
                .toUriString();
    }

    private String statsUri(final List<String> uris, final boolean unique) {
        return UriComponentsBuilder.newInstance()
                .scheme(SCHEMA)
                .host(HOST)
                .port(PORT)
                .path("/stats")
                .queryParam("start", START.format(FORMATTER))
                .queryParam("end", END.format(FORMATTER))
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .build()
                .encode()
                .toUriString();
    }
}