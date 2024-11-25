package ru.practicum.ewm.stats;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.ewm.common.AbstractControllerTest;
import ru.practicum.ewm.common.LogListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;
import static ru.practicum.ewm.common.TestUtils.assertLogs;
import static ru.practicum.ewm.stats.TestModels.NO_ID;
import static ru.practicum.ewm.stats.TestModels.makeTestHit;
import static ru.practicum.ewm.stats.TestModels.makeTestHitDto;
import static ru.practicum.ewm.stats.TestModels.makeTestNewHitDto;

class HitControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(HitController.class);

    @Mock
    private HitService mockService;

    @Mock
    private HitMapper mockMapper;

    private HitController controller;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        inOrder = Mockito.inOrder(mockService, mockMapper);
        logListener.startListen();
        logListener.reset();
        controller = new HitController(mockService, mockMapper);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockService, mockMapper);
        super.tearDown();
    }

    @Test
    void whenAdd_ThenMapNewHitDtoToHitAndPassToServiceAndMapServiceResponseToDtoAndReturnItAndLog() throws Exception {
        when(mockMapper.mapToHit(any())).thenReturn(makeTestHit(NO_ID));
        when(mockService.addHit(any())).thenReturn(makeTestHit());
        when(mockMapper.mapToDto(any(Hit.class))).thenReturn(makeTestHitDto());

        final HitDto hitDto = controller.add(makeTestNewHitDto(), mockHttpRequest);

        inOrder.verify(mockMapper).mapToHit(makeTestNewHitDto());
        inOrder.verify(mockService).addHit(refEq(makeTestHit(NO_ID)));
        inOrder.verify(mockMapper).mapToDto(refEq(makeTestHit()));
        assertThat(hitDto, equalTo(makeTestHitDto()));
        assertLogs(logListener.getEvents(), "add.json", getClass());
    }
}