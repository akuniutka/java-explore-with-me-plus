package ru.practicum.ewm.compilation;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final EventService eventService;
    private final CompilationMapper mapper;
    private final CompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> getAll(Boolean pinned, PageRequest pageRequest) {
        BooleanExpression byPinned = pinned != null ? QCompilation.compilation.pinned.eq(pinned) : null;
        List<Compilation> compilations = compilationRepository.findAll(byPinned, pageRequest).getContent();
        return mapper.mapToDtos(compilations);
    }

    @Override
    public CompilationDto getById(long id) {
        final Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Compilation.class, id));
        return mapper.mapToDto(compilation);
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto save(final NewCompilationDto requestDto) {
        final List<Long> eventIds = requestDto.events();
        final List<Event> relatedEvents = eventService.getByIds(eventIds);
        final Compilation compilation = mapper.mapToCompilation(requestDto, relatedEvents);
        return mapper.mapToDto(compilationRepository.save(compilation));
    }

    @Override
    public void delete(long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException(Compilation.class, id);
        }
        compilationRepository.deleteById(id);
    }
}
