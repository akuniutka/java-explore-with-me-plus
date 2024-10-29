package ru.practicum.ewm.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public interface CategoryService {

    Category createCategory(@Valid Category category);

    Category getCategory(long id);

    List<Category> getCategories(@PositiveOrZero int from, @Positive int size);

    Category patchCategory(@Valid CategoryPatch patch);

    void deleteCategory(long id);

    boolean categoryExists(long id);
}
