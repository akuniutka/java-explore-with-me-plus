package ru.practicum.ewm.category;

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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.ewm.category.TestModels.CATEGORY_ID;
import static ru.practicum.ewm.category.TestModels.NO_ID;
import static ru.practicum.ewm.category.TestModels.makeTestCategory;
import static ru.practicum.ewm.category.TestModels.makeTestCategoryCreateDto;
import static ru.practicum.ewm.category.TestModels.makeTestCategoryDto;
import static ru.practicum.ewm.category.TestModels.makeTestCategoryPatch;
import static ru.practicum.ewm.category.TestModels.makeTestCategoryUpdateDto;
import static ru.practicum.ewm.common.TestUtils.assertLogs;

class CategoryAdminControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(CategoryAdminController.class);

    @Mock
    private CategoryService mockService;

    @Mock
    private CategoryMapperImpl mockMapper;

    private CategoryAdminController controller;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        inOrder = Mockito.inOrder(mockService, mockMapper);
        logListener.startListen();
        logListener.reset();
        controller = new CategoryAdminController(mockService, mockMapper);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockService, mockMapper);
        super.tearDown();
    }

    @Test
    void whenAdd_ThenMapCategoryCreateDtoToCategoryAndPassToServiceAndMapServiceResponseToDtoAndReturnItAndLog()
            throws Exception {
        when(mockMapper.mapToCategory(any(CategoryCreateDto.class))).thenReturn(makeTestCategory(NO_ID));
        when(mockService.add(any())).thenReturn(makeTestCategory());
        when(mockMapper.mapToDto(any(Category.class))).thenReturn(makeTestCategoryDto());

        final CategoryDto categoryDto = controller.add(makeTestCategoryCreateDto(), mockHttpRequest);

        inOrder.verify(mockMapper).mapToCategory(makeTestCategoryCreateDto());
        inOrder.verify(mockService).add(refEq(makeTestCategory(NO_ID)));
        inOrder.verify(mockMapper).mapToDto(refEq(makeTestCategory()));
        assertThat(categoryDto, equalTo(makeTestCategoryDto()));
        assertLogs(logListener.getEvents(), "add.json", getClass());
    }

    @Test
    void whenUpdate_ThenMapCategoryUpdateDtoToPatchAndPassToServiceAndMapServiceResponseToDtoAndReturnAndLog()
            throws Exception {
        when(mockMapper.mapToCategoryPatch(anyLong(), any())).thenReturn(makeTestCategoryPatch());
        when(mockService.update(any())).thenReturn(makeTestCategory());
        when(mockMapper.mapToDto(any(Category.class))).thenReturn(makeTestCategoryDto());

        final CategoryDto categoryDto = controller.update(CATEGORY_ID, makeTestCategoryUpdateDto(), mockHttpRequest);

        inOrder.verify(mockMapper).mapToCategoryPatch(CATEGORY_ID, makeTestCategoryUpdateDto());
        inOrder.verify(mockService).update(makeTestCategoryPatch());
        inOrder.verify(mockMapper).mapToDto(refEq(makeTestCategory()));
        assertThat(categoryDto, equalTo(makeTestCategoryDto()));
        assertLogs(logListener.getEvents(), "update.json", getClass());
    }

    @Test
    void whenRemove_ThenPassCategoryIdToServiceAndLog() throws Exception {

        controller.remove(CATEGORY_ID, mockHttpRequest);

        verify(mockService).removeById(CATEGORY_ID);
        assertLogs(logListener.getEvents(), "remove.json", getClass());
    }
}