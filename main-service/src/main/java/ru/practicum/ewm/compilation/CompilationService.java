package ru.practicum.ewm.compilation;

import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAll(Boolean pinned, PageRequest pageRequest);

    CompilationDto getById(long id);

    CompilationDto save(NewCompilationDto requestDto);

    void delete(long id);
}
