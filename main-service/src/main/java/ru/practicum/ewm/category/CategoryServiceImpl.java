package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    @Override
    public Category add(final Category category) {
        final Category savedCategory = repository.save(category);
        log.info("Added category with id = {}: {}", savedCategory.getId(), savedCategory);
        return savedCategory;
    }

    @Override
    public Category getById(final long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(Category.class, id));
    }

    @Override
    public List<Category> getAllInWindow(final int windowSize, final int windowIndex) {
        final Pageable page = PageRequest.of(windowIndex, windowSize, Sort.by("id"));
        return repository.findAll(page).getContent();
    }

    @Override
    public Category update(final CategoryPatch patch) {
        final Category category = getById(patch.categoryId());
        Optional.ofNullable(patch.name()).ifPresent(category::setName);
        final Category savedCategory = repository.save(category);
        log.info("Updated category with id = {}: {}", savedCategory.getId(), savedCategory);
        return savedCategory;
    }

    @Transactional
    @Override
    public void removeById(final long id) {
        if (repository.delete(id) == 0) {
            throw new NotFoundException(Category.class, id);
        }
        log.info("Removed category with id = {}", id);
    }
}
