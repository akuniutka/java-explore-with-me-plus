package ru.practicum.ewm.category;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class CategoryMapperImpl implements CategoryMapper {

    Category mapToCategory(final CategoryCreateDto dto) {
        if (dto == null) {
            return null;
        }
        final Category category = new Category();
        category.setName(dto.name());
        return category;
    }

    CategoryPatch mapToCategoryPatch(final long id, final CategoryUpdateDto dto) {
        return new CategoryPatch(id, Optional.ofNullable(dto).map(CategoryUpdateDto::name).orElse(null));
    }

    @Override
    public Category mapToCategory(final Long id) {
        if (id == null) {
            return null;
        }
        final Category category = new Category();
        category.setId(id);
        return category;
    }

    @Override
    public CategoryDto mapToDto(final Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryDto(category.getId(), category.getName());
    }

    @Override
    public List<CategoryDto> mapToDto(final List<Category> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(this::mapToDto)
                .toList();
    }
}
