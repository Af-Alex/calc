package com.alexaf.salarycalc.goal;

import com.alexaf.salarycalc.goal.dto.GoalDto;
import com.alexaf.salarycalc.goal.repository.Goal;
import com.alexaf.salarycalc.goal.repository.GoalRepository;
import com.alexaf.salarycalc.user.UserService;
import com.alexaf.salarycalc.user.repository.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final UserService userService;

    public List<Goal> findAllByUserId(UUID userId) {
        return goalRepository.findByUser_Id(userId);
    }

    public List<Goal> findActiveByUserIdSortByPriority(UUID userId) {
        return goalRepository.findByActiveTrueAndUser_IdOrderByPriorityAsc(userId);
    }

    public Optional<Goal> findById(UUID id) {
        return goalRepository.findById(id);
    }

    public Goal save(Goal goal) {
        return goalRepository.save(goal);
    }

    public GoalDto create(@Valid GoalDto goalDto) {
        var entity = goalMapper.createFromDto(goalDto);
        entity.setUser(userService.getById(goalDto.getUserId()));

        return goalMapper.toDto(goalRepository.save(entity));
    }

    public void createDefaultGoals(UUID userId) {
        User user = userService.getById(userId);

        // обязательно откладывать 10% на подушку
        Goal airbagGoal = new Goal(user, GoalType.MONTHLY_PERCENTAGE_WITHOUT_DEADLINE, "Подушка безопасности");
        airbagGoal.setMonthlyAmount(new BigDecimal("0.1"));

        // откладывать 50000 на обязательные расходы
        Goal reqiredGoal = new Goal(user, GoalType.FIXED_AMOUNT_WITHOUT_DEADLINE, "Обязательные расходы");
        reqiredGoal.setMonthlyAmount(new BigDecimal("50000"));

        goalRepository.saveAll(List.of(airbagGoal, reqiredGoal));
    }

}
