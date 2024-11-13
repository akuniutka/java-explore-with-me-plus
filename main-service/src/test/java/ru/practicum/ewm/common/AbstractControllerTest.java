package ru.practicum.ewm.common;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AbstractControllerTest {

    protected static final String METHOD = "POST";
    protected static final String URI = "http://somehost/home";
    protected static final String QUERY_STRING = "value=none";

    protected AutoCloseable openMocks;

    @Mock
    protected HttpServletRequest mockHttpRequest;

    protected InOrder inOrder;

    @BeforeEach
    protected void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        Mockito.when(mockHttpRequest.getMethod()).thenReturn(METHOD);
        Mockito.when(mockHttpRequest.getRequestURI()).thenReturn(URI);
        Mockito.when(mockHttpRequest.getQueryString()).thenReturn(QUERY_STRING);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getMethod();
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getRequestURI();
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getQueryString();
        Mockito.verifyNoMoreInteractions(mockHttpRequest);
        openMocks.close();
    }
}
