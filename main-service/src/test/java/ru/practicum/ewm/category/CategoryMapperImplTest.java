package ru.practicum.ewm.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static ru.practicum.ewm.category.TestModels.CATEGORY_ID;
import static ru.practicum.ewm.category.TestModels.NO_ID;
import static ru.practicum.ewm.category.TestModels.NO_NAME;
import static ru.practicum.ewm.category.TestModels.makeTestCategory;
import static ru.practicum.ewm.category.TestModels.makeTestCategoryCreateDto;
import static ru.practicum.ewm.category.TestModels.makeTestCategoryDto;
import static ru.practicum.ewm.category.TestModels.makeTestCategoryPatch;
import static ru.practicum.ewm.category.TestModels.makeTestCategoryUpdateDto;

class CategoryMapperImplTest {

    private CategoryMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new CategoryMapperImpl();
    }

    @Test
    void whenMapNotNullCategoryCreateDto_ThenReturnCategory() {

        final Category category = mapper.mapToCategory(makeTestCategoryCreateDto());

        assertThat(category, samePropertyValuesAs(makeTestCategory(NO_ID)));
    }

    @Test
    void whenMapNullCategoryCreateDto_ThenReturnNull() {

        final Category category = mapper.mapToCategory((CategoryCreateDto) null);

        assertThat(category, nullValue());
    }

    @Test
    void whenMapNotNullCategoryUpdateDto_ThenReturnCategoryPatch() {

        final CategoryPatch patch = mapper.mapToCategoryPatch(CATEGORY_ID, makeTestCategoryUpdateDto());

        assertThat(patch, equalTo(makeTestCategoryPatch()));
    }

    @Test
    void whenMapNullCategoryUpdateDto_ThenReturnCategoryPatchWithCategoryIdOnly() {

        final CategoryPatch patch = mapper.mapToCategoryPatch(CATEGORY_ID, null);

        assertThat(patch, equalTo(makeTestCategoryPatch(NO_NAME)));
    }

    @Test
    void whenMapNotNullId_ThenReturnCategoryWithIdOnly() {

        final Category category = mapper.mapToCategory(CATEGORY_ID);

        assertThat(category, samePropertyValuesAs(makeTestCategory(NO_NAME)));
    }

    @Test
    void whenMapNullId_ThenReturnNull() {

        final Category category = mapper.mapToCategory((Long) null);

        assertThat(category, nullValue());
    }

    @Test
    void whenMapNotNullCategory_ThenReturnSingleDto() {

        final CategoryDto dto = mapper.mapToDto(makeTestCategory());

        assertThat(dto, equalTo(makeTestCategoryDto()));
    }

    @Test
    void whenMapNullCategory_ThenReturnNull() {

        final CategoryDto dto = mapper.mapToDto((Category) null);

        assertThat(dto, nullValue());
    }

    @Test
    void whenMapNotNullCategoryList_ThenReturnDtoList() {

        final List<CategoryDto> dtos = mapper.mapToDto(List.of(makeTestCategory()));

        assertThat(dtos, contains(makeTestCategoryDto()));
    }

    @Test
    void whenMapNullCategoryList_ThenReturnNull() {

        final List<CategoryDto> dtos = mapper.mapToDto((List<Category>) null);

        assertThat(dtos, nullValue());
    }
}