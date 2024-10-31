package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserService;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
class EventServiceImpl implements EventService {

    private final UserService userService;
    private final CategoryService categoryService;
    private final EventRepository repository;

    @Override
    @Transactional
    public Event add(final Event event) {
        event.setInitiator(fetchUser(event.getInitiator()));
        event.setCategory(fetchCategory(event.getCategory()));
        final Event savedEvent = repository.save(event);
        log.info("Added event with id = {}: {}", savedEvent.getId(), savedEvent);
        return savedEvent;
    }

    private User fetchUser(final User user) {
        if (user == null || user.getId() == null) {
            throw new AssertionError();
        }
        return userService.getById(user.getId());
    }

    private Category fetchCategory(final Category category) {
        if (category == null || category.getId() == null) {
            throw new AssertionError();
        }
        return categoryService.getById(category.getId());
    }
}
