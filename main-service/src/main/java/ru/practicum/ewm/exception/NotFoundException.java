package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final String modelName;
    private final Long modelId;

    public NotFoundException(final String modelName, final Long modelId) {
        super("%s with id = %s not found".formatted(modelName, modelId));
        this.modelName = modelName;
        this.modelId = modelId;
    }

    public <T> NotFoundException(final Class<T> modelClass, final Long modelId) {
        this(modelClass.getSimpleName(), modelId);
    }
}
