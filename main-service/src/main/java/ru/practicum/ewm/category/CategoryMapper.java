package ru.practicum.ewm.category;

import java.util.List;

public interface CategoryMapper {

    CategoryDto mapToDto(Category category);

    List<CategoryDto> mapToDto(List<Category> categories);
}
