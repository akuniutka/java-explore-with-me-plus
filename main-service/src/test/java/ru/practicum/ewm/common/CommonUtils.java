package ru.practicum.ewm.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class CommonUtils {

    public static final long CATEGORY_ID = 1L;
    public static final String CATEGORY_NAME = "sports";

    private static final ObjectMapper mapper = new ObjectMapper();

    private CommonUtils() {
    }

    public static <T> Set<String> validate(final T target) {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            final Validator validator = validatorFactory.getValidator();
            return validator.validate(target).stream()
                    .map(ConstraintViolation::getPropertyPath)
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }
    }

    public static String loadJson(final String filename, final Class<?> clazz) throws IOException {
        final String expandedFilename = clazz.getSimpleName().toLowerCase() + "/" + filename;
        final ClassPathResource resource = new ClassPathResource(expandedFilename, clazz);
        return Files.readString(resource.getFile().toPath());
    }

    public static void assertLogs(final List<LogListener.Event> events, final String filename,
            final Class<?> clazz) throws IOException, JSONException {
        final String expected = loadJson(filename, clazz);
        final String actual = mapper.writeValueAsString(events);
        JSONAssert.assertEquals(expected, actual, false);
    }
}