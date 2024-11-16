package ru.practicum.ewm.category;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.common.LogListener;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static ru.practicum.ewm.category.TestModels.CATEGORY_ID;
import static ru.practicum.ewm.category.TestModels.DEFAULT_PAGE;
import static ru.practicum.ewm.category.TestModels.DEFAULT_WINDOW_INDEX;
import static ru.practicum.ewm.category.TestModels.DEFAULT_WINDOW_SIZE;
import static ru.practicum.ewm.category.TestModels.NO_ID;
import static ru.practicum.ewm.category.TestModels.NO_NAME;
import static ru.practicum.ewm.category.TestModels.makeTestCategory;
import static ru.practicum.ewm.category.TestModels.makeTestCategoryPatch;
import static ru.practicum.ewm.common.CommonUtils.assertLogs;

class CategoryServiceImplTest {

    private static final LogListener logListener = new LogListener(CategoryServiceImpl.class);

    private AutoCloseable openMocks;

    @Mock
    private CategoryRepository mockRepository;

    private InOrder inOrder;

    private CategoryService service;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        inOrder = Mockito.inOrder(mockRepository);
        logListener.startListen();
        logListener.reset();
        service = new CategoryServiceImpl(mockRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockRepository);
        openMocks.close();
    }

    @Test
    void whenAddNewCategory_ThenPassItToRepositoryAndReturnRepositoryResponseAndLog() throws Exception {
        when(mockRepository.save(any())).thenReturn(makeTestCategory());

        final Category category = service.add(makeTestCategory(NO_ID));

        verify(mockRepository).save(argThat(samePropertyValuesAs(makeTestCategory(NO_ID))));
        assertThat(category, samePropertyValuesAs(makeTestCategory()));
        assertLogs(logListener.getEvents(), "add.json", getClass());
    }

    @Test
    void whenGetExistingCategoryById_ThenRetrieveCategoryFromRepositoryAndReturnIt() {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(makeTestCategory()));

        final Category category = service.getById(CATEGORY_ID);

        verify(mockRepository).findById(CATEGORY_ID);
        assertThat(category, samePropertyValuesAs(makeTestCategory()));
    }

    @Test
    void whenGetNotExistingCategoryById_ThenLookForCategoryInRepositoryAndThrowException() {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getById(CATEGORY_ID));

        verify(mockRepository).findById(CATEGORY_ID);
        assertThat(exception.getModelName(), is("Category"));
        assertThat(exception.getModelIds(), contains(CATEGORY_ID));
    }

    @Test
    void whenGetSliceOfCategories_ThenRetrieveSliceFromRepositoryAndReturnIt() {
        when(mockRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(makeTestCategory())));

        final List<Category> categories = service.getAllInWindow(DEFAULT_WINDOW_SIZE, DEFAULT_WINDOW_INDEX);

        verify(mockRepository).findAll(DEFAULT_PAGE);
        assertThat(categories, contains(samePropertyValuesAs(makeTestCategory())));
    }

    @Test
    void whenGetSliceOfCategoriesAndItIsEmpty_ThenRetrieveSliceFromRepositoryAndReturnEmptyList() {
        when(mockRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        final List<Category> categories = service.getAllInWindow(DEFAULT_WINDOW_SIZE, DEFAULT_WINDOW_INDEX);

        verify(mockRepository).findAll(DEFAULT_PAGE);
        assertThat(categories, empty());
    }

    @Test
    void whenUpdateCategory_ThenGetItFromRepositoryAndPassUpdatedToRepositoryAndReturnRepositoryResponseAndLog()
            throws Exception {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(makeTestCategory(NO_NAME)));
        when(mockRepository.save(any())).thenReturn(makeTestCategory());

        final Category category = service.update(makeTestCategoryPatch());

        inOrder.verify(mockRepository).findById(CATEGORY_ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(makeTestCategory())));
        assertThat(category, samePropertyValuesAs(makeTestCategory()));
        assertLogs(logListener.getEvents(), "update.json", getClass());
    }

    @Test
    void whenUpdateCategoryAndNothingToPatch_ThenGetCategoryFromRepositoryAndPassBackAndReturnRepositoryResponseAndLog()
            throws Exception {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(makeTestCategory()));
        when(mockRepository.save(any())).thenReturn(makeTestCategory());

        final Category category = service.update(makeTestCategoryPatch(NO_NAME));

        inOrder.verify(mockRepository).findById(CATEGORY_ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(makeTestCategory())));
        assertThat(category, samePropertyValuesAs(makeTestCategory()));
        assertLogs(logListener.getEvents(), "update_nothing.json", getClass());
    }

    @Test
    void whenUpdateNotExistingCategory_ThenLookForItInRepositoryAndThrowException() {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.update(makeTestCategoryPatch()));

        verify(mockRepository).findById(CATEGORY_ID);
        assertThat(exception.getModelName(), is("Category"));
        assertThat(exception.getModelIds(), contains(CATEGORY_ID));
    }

    @Test
    void whenRemoveExistingCategoryById_ThenDeleteItInRepositoryAndLog() throws Exception {
        when(mockRepository.delete(anyLong())).thenReturn(1);

        service.removeById(CATEGORY_ID);

        verify(mockRepository).delete(CATEGORY_ID);
        assertLogs(logListener.getEvents(), "remove_by_id.json", getClass());
    }

    @Test
    void whenRemoveNotExistingCategoryById_ThenTryDeleteItInRepositoryAndThrowException() {
        when(mockRepository.delete(anyLong())).thenReturn(0);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.removeById(CATEGORY_ID));

        verify(mockRepository).delete(CATEGORY_ID);
        assertThat(exception.getModelName(), is("Category"));
        assertThat(exception.getModelIds(), contains(CATEGORY_ID));
    }
}