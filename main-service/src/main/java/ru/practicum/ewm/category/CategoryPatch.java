package ru.practicum.ewm.category;

import jakarta.validation.constraints.Size;
import ru.practicum.ewm.validation.NotBlankOrNull;

public record CategoryPatch(

        long categoryId,

        @NotBlankOrNull
        @Size(max = 50)
        String name
) {

}
