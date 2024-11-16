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
import ru.practicum.ewm.common.ClockConfig;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.category.TestModels.CATEGORY_ID;
import static ru.practicum.ewm.category.TestModels.NO_ID;
import static ru.practicum.ewm.category.TestModels.makeTestCategory;
import static ru.practicum.ewm.category.TestModels.makeTestCategoryCreateDto;
import static ru.practicum.ewm.category.TestModels.makeTestCategoryDto;
import static ru.practicum.ewm.category.TestModels.makeTestCategoryPatch;
import static ru.practicum.ewm.category.TestModels.makeTestCategoryUpdateDto;
import static ru.practicum.ewm.common.CommonUtils.loadJson;

@WebMvcTest(controllers = CategoryAdminController.class)
@ContextConfiguration(classes = ClockConfig.class)
class CategoryAdminControllerIT {

    private static final String BASE_PATH = "/admin/categories";

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
    void whenPostAtBasePath_ThenInvokeAddMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("add_request.json", getClass());
        final String responseBody = loadJson("add_response.json", getClass());
        when(mockMapper.mapToCategory(any(CategoryCreateDto.class))).thenReturn(makeTestCategory(NO_ID));
        when(mockService.add(any())).thenReturn(makeTestCategory());
        when(mockMapper.mapToDto(any(Category.class))).thenReturn(makeTestCategoryDto());

        mvc.perform(post(BASE_PATH)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockMapper).mapToCategory(makeTestCategoryCreateDto());
        inOrder.verify(mockService).add(refEq(makeTestCategory(NO_ID)));
        inOrder.verify(mockMapper).mapToDto(refEq(makeTestCategory()));
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

    @Test
    void whenPatchAtBasePathWithId_ThenInvokeUpdateMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("update_request.json", getClass());
        final String responseBody = loadJson("update_response.json", getClass());
        when(mockMapper.mapToCategoryPatch(anyLong(), any())).thenReturn(makeTestCategoryPatch());
        when(mockService.update(any())).thenReturn(makeTestCategory());
        when(mockMapper.mapToDto(any(Category.class))).thenReturn(makeTestCategoryDto());

        mvc.perform(patch(BASE_PATH + "/" + CATEGORY_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockMapper).mapToCategoryPatch(CATEGORY_ID, makeTestCategoryUpdateDto());
        inOrder.verify(mockService).update(makeTestCategoryPatch());
        inOrder.verify(mockMapper).mapToDto(refEq(makeTestCategory()));
    }

    @Test
    void whenPatchAtBasePathWithId_ThenValidateDto() throws Exception {
        final String requestBody = loadJson("update_wrong_format_request.json", getClass());
        final String responseBody = loadJson("update_wrong_format_response.json", getClass());

        mvc.perform(patch(BASE_PATH + "/" + CATEGORY_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));
    }

    @Test
    void whenDeleteAtBasePathWithId_ThenInvokeRemoveMethodAndProcessResponseStatus() throws Exception {

        mvc.perform(delete(BASE_PATH + "/" + CATEGORY_ID))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(mockService).removeById(CATEGORY_ID);
    }
}