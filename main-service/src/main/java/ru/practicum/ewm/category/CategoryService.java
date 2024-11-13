package ru.practicum.ewm.category;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface CategoryService {

    Category add(@NotNull Category category);

    Category getById(long id);

    List<Category> getAllInWindow(int windowSize, int windowIndex);

    Category update(@NotNull CategoryPatch patch);

    void removeById(long id);
}
