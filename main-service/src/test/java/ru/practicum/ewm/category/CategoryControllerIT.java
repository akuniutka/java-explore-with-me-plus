package ru.practicum.ewm.category;

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
import ru.practicum.ewm.configuration.ClockConfig;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.category.TestModels.CATEGORY_ID;
import static ru.practicum.ewm.category.TestModels.DEFAULT_WINDOW_INDEX;
import static ru.practicum.ewm.category.TestModels.DEFAULT_WINDOW_SIZE;
import static ru.practicum.ewm.category.TestModels.makeTestCategory;
import static ru.practicum.ewm.category.TestModels.makeTestCategoryDto;
import static ru.practicum.ewm.common.TestUtils.loadJson;
import static ru.practicum.ewm.common.TestUtils.refContains;

@WebMvcTest(controllers = CategoryController.class)
@ContextConfiguration(classes = ClockConfig.class)
class CategoryControllerIT {

    private static final String BASE_PATH = "/categories";
    private static final int FROM = 29;
    private static final int SIZE = 10;
    private static final int WRONG_FROM = -1;
    private static final int WRONG_SIZE = 0;
    private static final int WINDOW_SIZE = 10;
    private static final int WINDOW_INDEX = 2;

    @MockBean
    private CategoryService mockService;

    @MockBean
    private CategoryMapperImpl mockMapper;

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
    void whenGetAtBasePath_ThenInvokeGetAllMethodAndProcessResponse() throws Exception {
        final String responseBody = loadJson("get_list.json", getClass());
        when(mockService.getAllInWindow(anyInt(), anyInt())).thenReturn(List.of(makeTestCategory()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestCategoryDto()));

        mvc.perform(get(BASE_PATH)
                        .param("from", String.valueOf(FROM))
                        .param("size", String.valueOf(SIZE))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).getAllInWindow(WINDOW_SIZE, WINDOW_INDEX);
        inOrder.verify(mockMapper).mapToDto(refContains(makeTestCategory()));
    }

    @Test
    void whenGetAtBasePathWithoutParams_ThenInvokeGetAllMethodWithDefaultParams() throws Exception {
        final String responseBody = loadJson("get_list_with_default_params.json", getClass());
        when(mockService.getAllInWindow(anyInt(), anyInt())).thenReturn(List.of(makeTestCategory()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestCategoryDto()));

        mvc.perform(get(BASE_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).getAllInWindow(DEFAULT_WINDOW_SIZE, DEFAULT_WINDOW_INDEX);
        inOrder.verify(mockMapper).mapToDto(refContains(makeTestCategory()));
    }

    @Test
    void whenGetAtBasePath_ThenValidateParams() throws Exception {
        final String responseBody = loadJson("get_list_with_wrong_params.json", getClass());

        mvc.perform(get(BASE_PATH)
                        .param("from", String.valueOf(WRONG_FROM))
                        .param("size", String.valueOf(WRONG_SIZE))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));
    }

    @Test
    void whenGetAtBasePathWithId_ThenInvokeGetMethodAndProcessResponse() throws Exception {
        final String responseBody = loadJson("get_single.json", getClass());
        when(mockService.getById(anyLong())).thenReturn(makeTestCategory());
        when(mockMapper.mapToDto(any(Category.class))).thenReturn(makeTestCategoryDto());

        mvc.perform(get(BASE_PATH + "/" + CATEGORY_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).getById(CATEGORY_ID);
        inOrder.verify(mockMapper).mapToDto(refEq(makeTestCategory()));
    }
}