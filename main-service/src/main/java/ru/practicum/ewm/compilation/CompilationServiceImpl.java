package ru.practicum.ewm.compilation;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationMapper mapper;
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getAll(Boolean pinned, PageRequest pageRequest) {
        final BooleanExpression byPinned = pinned != null
                ? QCompilation.compilation.pinned.eq(pinned)
                : Expressions.TRUE; // если pinned = null ищем все подборки без фильтрации
        final List<Compilation> compilations = compilationRepository.findAll(byPinned, pageRequest).getContent();
        return mapper.mapToDto(compilations);
    }

    @Override
    public CompilationDto getById(long id) {
        final Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Compilation.class, id));
        return mapper.mapToDto(compilation);
    }

    @Transactional
    @Override
    public CompilationDto save(final NewCompilationDto requestDto) {
        final Set<Long> eventIds = requestDto.events();
        final Set<Event> relatedEvents = eventRepository.findAllByIdIn(eventIds);
        final Compilation compilation = mapper.mapToCompilation(requestDto, relatedEvents);
        return mapper.mapToDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public void delete(long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException(Compilation.class, id);
        }
        compilationRepository.deleteById(id);
    }

    @Transactional
    @Override
    public CompilationDto update(long id, UpdateCompilationRequest requestDto) {
        final Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Compilation.class, id));
        if (requestDto.title() != null) {
            compilation.setTitle(requestDto.title());
        }
        if (requestDto.pinned() != null) {
            compilation.setPinned(requestDto.pinned());
        }
        if (requestDto.events() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(requestDto.events()));
        }
        final Compilation updatedCompilation = compilationRepository.save(compilation);
        return mapper.mapToDto(updatedCompilation);
    }
}
