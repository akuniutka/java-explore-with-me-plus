package ru.practicum.ewm.exception;

import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class NotFoundException extends RuntimeException {

    private final String modelName;
    private final Set<Long> modelIds;

    public NotFoundException(final String modelName, final Long modelId) {
        this(modelName, Set.of(modelId));
    }

    public <T> NotFoundException(final Class<T> modelClass, final Long modelId) {
        this(modelClass.getSimpleName(), modelId);
    }

    public NotFoundException(final String modelName, final Set<Long> modelIds) {
        Objects.requireNonNull(modelIds);
        this.modelName = modelName;
        this.modelIds = modelIds.stream()
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public <T> NotFoundException(final Class<T> modelClass, final Set<Long> modelIds) {
        this(modelClass.getSimpleName(), modelIds);
    }

    @Override
    public String getMessage() {
        if (modelIds.size() == 1) {
            return "%s with id = %s not found".formatted(modelName, modelIds.iterator().next());
        }
        final String modelIdsStr = modelIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        final String modelNamePlural = "Category".equals(modelName) ? "Categories" : modelName + "s";
        return "%s with id = %s not found".formatted(modelNamePlural, modelIdsStr);
    }
}
