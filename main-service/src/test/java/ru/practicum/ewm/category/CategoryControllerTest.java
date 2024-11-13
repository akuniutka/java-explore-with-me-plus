package ru.practicum.ewm.category;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.ewm.common.AbstractControllerTest;
import ru.practicum.ewm.common.LogListener;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static ru.practicum.ewm.category.TestModels.CATEGORY_ID;
import static ru.practicum.ewm.category.TestModels.makeTestCategory;
import static ru.practicum.ewm.category.TestModels.makeTestCategoryDto;
import static ru.practicum.ewm.common.CommonUtils.assertLogs;

class CategoryControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(CategoryController.class);
    private static final int FROM = 29;
    private static final int SIZE = 10;
    private static final int WINDOW_SIZE = 10;
    private static final int WINDOW_INDEX = 2;

    @Mock
    private CategoryService mockService;

    @Mock
    private CategoryMapperImpl mockMapper;

    private CategoryController controller;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        inOrder = Mockito.inOrder(mockService, mockMapper);
        logListener.startListen();
        logListener.reset();
        controller = new CategoryController(mockService, mockMapper);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockService, mockMapper);
        super.tearDown();
    }

    @Test
    void whenGetAllCategories_ThenPassWindowParamsToServiceAndMapServiceResponseToDtosAndReturnItAndLog()
            throws Exception {
        when(mockService.getAllInWindow(WINDOW_SIZE, WINDOW_INDEX)).thenReturn(List.of(makeTestCategory()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestCategoryDto()));

        final List<CategoryDto> categoryDtos = controller.getAll(FROM, SIZE, mockHttpRequest);

        inOrder.verify(mockService).getAllInWindow(WINDOW_SIZE, WINDOW_INDEX);
        inOrder.verify(mockMapper).mapToDto(argThat((List<Category> categories) ->
                contains(samePropertyValuesAs(makeTestCategory())).matches(categories)));
        assertThat(categoryDtos, contains(makeTestCategoryDto()));
        assertLogs(logListener.getEvents(), "get_list.json", getClass());
    }

    @Test
    void whenGetAllCategoriesAndListIsEmpty_ThenPassWindowParamsToServiceAndMapServiceResponseToDtosAndReturnItAndLog()
            throws Exception {
        when(mockService.getAllInWindow(WINDOW_SIZE, WINDOW_INDEX)).thenReturn(List.of());
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of());

        final List<CategoryDto> categoryDtos = controller.getAll(FROM, SIZE, mockHttpRequest);

        inOrder.verify(mockService).getAllInWindow(WINDOW_SIZE, WINDOW_INDEX);
        inOrder.verify(mockMapper).mapToDto(List.of());
        assertThat(categoryDtos, empty());
        assertLogs(logListener.getEvents(), "get_list_empty.json", getClass());
    }

    @Test
    void whenGetCategoryById_ThenPassIdToServiceAndMapServiceResponseToDtoAndReturnItAndLog() throws Exception {
        when(mockService.getById(CATEGORY_ID)).thenReturn(makeTestCategory());
        when(mockMapper.mapToDto(any(Category.class))).thenReturn(makeTestCategoryDto());

        final CategoryDto categoryDto = controller.get(CATEGORY_ID, mockHttpRequest);

        inOrder.verify(mockService).getById(CATEGORY_ID);
        inOrder.verify(mockMapper).mapToDto(argThat((Category category) ->
                samePropertyValuesAs(makeTestCategory()).matches(category)));
        assertThat(categoryDto, equalTo(makeTestCategoryDto()));
        assertLogs(logListener.getEvents(), "get_single.json", getClass());
    }
}