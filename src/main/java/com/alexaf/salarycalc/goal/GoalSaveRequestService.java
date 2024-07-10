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

import java.util.UUID;

import static java.lang.String.format;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class GoalSaveRequestService {

    private final GoalSaveRequestRepository repository;
    private final GoalSaveRequestMapper mapper;
    private final UserService userService;
    private final GoalSaveRequestMapper goalSaveRequestMapper;

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

    public GoalSaveRequestDto getById(UUID id) {
        return repository.findById(id)
                .map(goalSaveRequestMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(GoalSaveRequest.class, id));
    }

    public UUID create(@Valid GoalSaveRequestDto dto) {
        GoalSaveRequest entity = mapper.createFromDto(dto);

        entity.setUser(userService.getById(dto.getUserId()));

        return repository.save(entity).getId();
    }

    public UUID update(@Valid GoalSaveRequestDto dto) {
        GoalSaveRequest entity = mapper.createFromDto(dto);

        entity.setUser(userService.getById(dto.getUserId()));

        return repository.save(entity).getId();
    }

}
