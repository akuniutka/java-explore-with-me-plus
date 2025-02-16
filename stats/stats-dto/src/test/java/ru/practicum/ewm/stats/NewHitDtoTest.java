package ru.practicum.ewm.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;
import java.time.LocalDateTime;

class NewHitDtoTest {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void testNewHitDto() throws Exception {
        final ClassPathResource resource = new ClassPathResource("new_hit_dto.json", getClass());
        final String expected = Files.readString(resource.getFile().toPath());
        final NewHitDto dto = NewHitDto.builder()
                .app("mainService")
                .uri("endpointA")
                .ip("127.0.0.1")
                .timestamp(LocalDateTime.of(2000, 1, 31, 13, 30, 55))
                .build();

        final String actual = objectMapper.writeValueAsString(dto);

        JSONAssert.assertEquals(expected, actual, false);
    }
}