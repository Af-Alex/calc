package com.alexaf.salarycalc.goal;

import com.alexaf.salarycalc.exception.EntityNotFoundException;
import com.alexaf.salarycalc.goal.dto.GoalSaveRequestDto;
import com.alexaf.salarycalc.goal.repository.GoalSaveRequest;
import com.alexaf.salarycalc.goal.repository.GoalSaveRequestRepository;
import com.alexaf.salarycalc.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class GoalSaveRequestService {

    private final GoalSaveRequestRepository repository;
    private final UserService userService;
    private final GoalSaveRequestMapper goalSaveRequestMapper;
    private final GoalService goalService;

    public GoalSaveRequest getEntityByUserId(UUID userId) {
        return repository.findByUser_Id(userId).orElseThrow(
                () -> new EntityNotFoundException(format("%s with userId [%s] not found", GoalSaveRequest.class.getSimpleName(), userId))
        );
    }

    public GoalSaveRequest getEntityById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(GoalSaveRequest.class, id));
    }

    public GoalSaveRequestDto getByUserId(UUID userId) {
        return repository.findByUser_Id(userId)
                .map(goalSaveRequestMapper::toDto)
                .orElseThrow(
                        () -> new EntityNotFoundException(format("%s with userId [%s] not found", GoalSaveRequest.class.getSimpleName(), userId))
        );
    }

    public Optional<GoalSaveRequestDto> findByUserId(UUID userId) {
        return repository.findByUser_Id(userId).map(goalSaveRequestMapper::toDto);
    }

    public GoalSaveRequestDto getById(UUID id) {
        return repository.findById(id)
                .map(goalSaveRequestMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(GoalSaveRequest.class, id));
    }

    public GoalSaveRequestDto create(@Valid GoalSaveRequestDto dto) {
        GoalSaveRequest entity = goalSaveRequestMapper.createFromDto(dto);

        entity.setUser(userService.getById(dto.getUserId()));

        return goalSaveRequestMapper.toDto(repository.save(entity));
    }

    public GoalSaveRequestDto update(@Valid GoalSaveRequestDto dto) {
        GoalSaveRequest entity = goalSaveRequestMapper.partialUpdate(
                getEntityByUserId(dto.getUserId()),
                goalSaveRequestMapper.createFromDto(dto)
        );
        return goalSaveRequestMapper.toDto(repository.save(entity));
    }

    public void deleteById(UUID goalId) {
        repository.deleteById(goalId);
    }

    /**
     * Завершение создания GoalSaveRequest состоит из нескольких этапов:
     *     <p>1) Преобразование запроса сохранения в сущность цели
     *     <p>2) Валидация цели
     *     <p>3) Сохранение цели
     *     <p>4) Удаление запроса на сохранение цели
     */
    public void finish(GoalSaveRequestDto goalSaveRequest) {
        var goalSaveRequestEntity = getEntityById(goalSaveRequest.getId());
        goalService.create(goalSaveRequestMapper.toGoalDto(goalSaveRequestEntity));
        deleteById(goalSaveRequest.getId());
    }
}
