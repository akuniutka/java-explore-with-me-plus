package ru.practicum.ewm.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public record NewCompilationDto(
        @UniqueElements
        List<Long> events,

        boolean pinned,

        @NotBlank
        @Size(min = 1, max = 50)
        String title) {
}
