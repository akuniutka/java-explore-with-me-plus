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

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.common.TestUtils.loadJson;
import static ru.practicum.ewm.stats.TestModels.NO_ID;
import static ru.practicum.ewm.stats.TestModels.makeTestHit;
import static ru.practicum.ewm.stats.TestModels.makeTestHitDto;
import static ru.practicum.ewm.stats.TestModels.makeTestNewHitDto;

@WebMvcTest(controllers = HitController.class)
@ContextConfiguration(classes = ClockConfig.class)
class HitControllerIT {

    private static final String BASE_PATH = "/hit";

    @MockBean
    private HitService mockService;

    @MockBean
    private HitMapper mockMapper;

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
    void whenPostAtBasePath_ThenInvokeAddMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("add_request.json", getClass());
        final String responseBody = loadJson("add_response.json", getClass());
        when(mockMapper.mapToHit(any())).thenReturn(makeTestHit(NO_ID));
        when(mockService.addHit(any())).thenReturn(makeTestHit());
        when(mockMapper.mapToDto(any(Hit.class))).thenReturn(makeTestHitDto());

        mvc.perform(post(BASE_PATH)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockMapper).mapToHit(makeTestNewHitDto());
        inOrder.verify(mockService).addHit(refEq(makeTestHit(NO_ID)));
        inOrder.verify(mockMapper).mapToDto(refEq(makeTestHit()));
    }

    @Test
    void whenPostAtBasePath_ThenValidateDto() throws Exception {
        final String requestBody = loadJson("add_wrong_format_request.json", getClass());
        final String responseBody = loadJson("add_wrong_format_response.json", getClass());

        mvc.perform(post(BASE_PATH)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));
    }
}