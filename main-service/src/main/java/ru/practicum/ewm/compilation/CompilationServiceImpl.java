package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final EventService eventService;
    private final CompilationMapper mapper;
    private final CompilationRepository repository;

    @Override
    public CompilationDto save(final NewCompilationDto requestDto) {
        final List<Long> eventIds = requestDto.events();
        final List<Event> relatedEvents = eventService.getByIds(eventIds);
        final Compilation compilation = mapper.mapToCompilation(requestDto, relatedEvents);
        return mapper.mapToDto(repository.save(compilation));
    }

    @Override
    public void delete(long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException(Compilation.class, id);
        }
        compilationRepository.deleteById(id);
    }
}
