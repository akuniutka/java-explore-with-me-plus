package ru.practicum.ewm.stats;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ProblemDetail;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.ewm.stats.TestUtils.assertLogs;

class ControllerExceptionHandlerTest {

    private static final LogListener logListener = new LogListener(ControllerExceptionHandler.class);

    private static final String METHOD = "POST";
    private static final String URI = "http://somehost/home";
    private static final String QUERY_STRING = "value=none";

    private AutoCloseable openMocks;

    @Mock
    private HttpServletRequest mockHttpRequest;

    private ControllerExceptionHandler handler;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        Mockito.when(mockHttpRequest.getMethod()).thenReturn(METHOD);
        Mockito.when(mockHttpRequest.getRequestURI()).thenReturn(URI);
        Mockito.when(mockHttpRequest.getQueryString()).thenReturn(QUERY_STRING);
        logListener.startListen();
        logListener.reset();
        handler = new ControllerExceptionHandler();
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verify(mockHttpRequest).getMethod();
        Mockito.verify(mockHttpRequest).getRequestURI();
        Mockito.verify(mockHttpRequest).getQueryString();
        Mockito.verifyNoMoreInteractions(mockHttpRequest);
        openMocks.close();
    }

    @Test
    void testHandleException() throws JSONException, IOException {
        final Exception exception = new Exception("Test exception");

        final ProblemDetail response = handler.handleException(exception, mockHttpRequest);

        assertThat(response.getStatus(), equalTo(500));
        assertThat(response.getDetail(), equalTo("Please contact site admin"));
        assertLogs(logListener.getEvents(), "exception.json", getClass());
    }
}