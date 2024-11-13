package ru.practicum.ewm.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.ewm.category.TestModels.DEFAULT_PAGE;

@DataJpaTest
class CategoryRepositoryIT {

    @Autowired
    private CategoryRepository repository;

    @Test
    void whenFindAllWithDefaultPageable_ThenReturnAllTwoCategories() {

        final List<String> first10CategoryNames = repository.findAll(DEFAULT_PAGE).stream()
                .map(Category::getName)
                .toList();

        assertThat(first10CategoryNames, contains("concerts", "cinemas"));
    }

    @Test
    void whenFindSecondPageOfSize1_ThenReturnCinemas() {
        final Pageable page = PageRequest.of(1, 1, Sort.by("id"));

        final List<String> secondCategoryName = repository.findAll(page).stream()
                .map(Category::getName)
                .toList();

        assertThat(secondCategoryName, contains("cinemas"));
    }

    @Test
    void whenFindFirstPageOfSize1_ThenReturnConcerts() {
        final Pageable page = PageRequest.of(0, 1, Sort.by("id"));

        final List<String> firstCategoryName = repository.findAll(page).stream()
                .map(Category::getName)
                .toList();

        assertThat(firstCategoryName, contains("concerts"));
    }

    @Test
    void whenSaveNewUniqueCategory_ThenExtendCategoriesList() {
        final Category category = new Category();
        category.setName("sports");

        repository.save(category);

        final List<String> first10CategoryNames = repository.findAll(DEFAULT_PAGE).stream()
                .map(Category::getName)
                .toList();
        assertThat(first10CategoryNames, contains("concerts", "cinemas", "sports"));
    }

    @Test
    void whenSaveNewCategoryWithExistingName_ThenThrowException() {
        final Category category = new Category();
        category.setName("concerts");

        assertThrows(DataIntegrityViolationException.class, () -> repository.save(category));
    }

    @Test
    void whenFindExistingCategoryById_ThenReturnItInOptional() {
        final Category category = new Category();
        category.setName("sports");
        final long id = repository.save(category).getId();

        final Optional<Category> found = repository.findById(id);

        assertThat(found.isPresent(), is(true));
        assertThat(found.get().getName(), is("sports"));
    }

    @Test
    void whenFindNotExistingCategoryById_ThenReturnEmptyOptional() {

        final Optional<Category> found = repository.findById(-1L);

        assertThat(found.isEmpty(), is(true));
    }

    @Test
    void whenSaveUpdatedCategoryAndNameStillUnique_ThenUpdateCategoryNameInList() {
        final Category category = new Category();
        category.setName("sports");
        final long id = repository.save(category).getId();

        final Category existingCategory = repository.findById(id).orElseThrow();
        existingCategory.setName("exhibitions");
        repository.save(existingCategory);
        repository.flush();

        final List<String> first10CategoryNames = repository.findAll(DEFAULT_PAGE).stream()
                .map(Category::getName)
                .toList();
        assertThat(first10CategoryNames, contains("concerts", "cinemas", "exhibitions"));
    }

    @Test
    void testSaveUpdatedCategoryAndNameAlreadyExist_ThenThrowException() {
        final Category category = new Category();
        category.setName("sports");
        final long id = repository.save(category).getId();

        final Category existingCategory = repository.findById(id).orElseThrow();
        existingCategory.setName("concerts");
        assertThrows(DataIntegrityViolationException.class, () -> {
            repository.save(existingCategory);
            repository.flush();
        });
    }

    @Test
    void whenDeleteExistingCategory_ThenReturn1() {
        final Category category = new Category();
        category.setName("sports");
        final long id = repository.save(category).getId();

        final int deletedCategoriesCount = repository.delete(id);

        final List<String> categoryNamesLeft = repository.findAll(DEFAULT_PAGE).stream()
                .map(Category::getName)
                .toList();
        assertThat(deletedCategoriesCount, is(1));
        assertThat(categoryNamesLeft, contains("concerts", "cinemas"));
    }

    @Test
    void whenDeleteNotExistingCategory_ThenReturn0() {

        final int deletedCategoriesCount = repository.delete(-1L);

        final List<String> categoryNamesLeft = repository.findAll(DEFAULT_PAGE).stream()
                .map(Category::getName)
                .toList();
        assertThat(deletedCategoriesCount, is(0));
        assertThat(categoryNamesLeft, contains("concerts", "cinemas"));
    }

    @Test
    void whenExistingEventPreventsDeletion_ThenThrowException() {

        assertThrows(DataIntegrityViolationException.class, () -> {
            repository.delete(1L);
            repository.flush();
        });
    }
}