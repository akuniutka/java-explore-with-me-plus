package ru.practicum.ewm.category;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

final class TestModels {

    public static final Long NO_ID = null;
    public static final long CATEGORY_ID = 1L;
    public static final String NO_NAME = null;
    public static final String CATEGORY_NAME = "sports";
    static final int DEFAULT_WINDOW_SIZE = 10;
    static final int DEFAULT_WINDOW_INDEX = 0;
    static final Pageable DEFAULT_PAGE = PageRequest.of(DEFAULT_WINDOW_INDEX, DEFAULT_WINDOW_SIZE, Sort.by("id"));

    private TestModels() {
    }

    static Category makeTestCategory() {
        return makeTestCategory(CATEGORY_ID);
    }

    static Category makeTestCategory(final Long id) {
        final Category category = new Category();
        category.setId(id);
        category.setName(CATEGORY_NAME);
        return category;
    }

    static Category makeTestCategory(final String name) {
        final Category category = new Category();
        category.setId(CATEGORY_ID);
        category.setName(name);
        return category;
    }

    static CategoryPatch makeTestCategoryPatch() {
        return makeTestCategoryPatch(CATEGORY_NAME);
    }

    static CategoryPatch makeTestCategoryPatch(final String name) {
        return new CategoryPatch(CATEGORY_ID, name);
    }

    static CategoryCreateDto makeTestCategoryCreateDto() {
        return new CategoryCreateDto(CATEGORY_NAME);
    }

    static CategoryUpdateDto makeTestCategoryUpdateDto() {
        return new CategoryUpdateDto(CATEGORY_NAME);
    }

    static CategoryDto makeTestCategoryDto() {
        return new CategoryDto(CATEGORY_ID, CATEGORY_NAME);
    }
}
