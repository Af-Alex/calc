package com.alexaf.salarycalc.service;

import com.alexaf.salarycalc.contribution.ContributionService;
import com.alexaf.salarycalc.contribution.dto.ContributionDto;
import com.alexaf.salarycalc.goal.GoalService;
import com.alexaf.salarycalc.goal.dto.GoalDto;
import com.alexaf.salarycalc.salary.SalaryService;
import com.alexaf.salarycalc.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.alexaf.salarycalc.utils.CalcUtils.percentOf;
import static java.math.RoundingMode.HALF_DOWN;
import static java.time.LocalDateTime.now;

@Service
@Slf4j
@RequiredArgsConstructor
public class DistributionService {

    private final UserService userService;
    private final GoalService goalService;
    private final SalaryService salaryService;
    private final ContributionService contributionService;

    @Transactional
    public Map<String, BigDecimal> distributeIncome(Long userId, BigDecimal income) {
        return distributeIncome(userService.getByTelegramId(userId).getId(), income);
    }

    @Transactional
    public Map<String, BigDecimal> distributeIncome(UUID userId, BigDecimal income) {
        // Получаем все активные цели пользователя
        List<GoalDto> goals = goalService.findActiveByUserIdSortByPriority(userId);
        if (goals.isEmpty()) {
            throw new IllegalArgumentException("No active goals found for user");
        }

        BigDecimal salary = salaryService.findActual(userId).getAmount();
        BigDecimal remainingAmount = income;
        Map<String, BigDecimal> prediction = new HashMap<>();

        for (GoalDto goal : goals) {
            log.debug("Обрабатываем цель \"{}\"", goal.getName());
            if (goal.getTargetAmount() != null && goal.getTargetAmount().compareTo(goal.getBalance()) <= 0) {
                log.debug("Цель уже выполнена");
                continue;
            }
            if (goal.getDeadline() != null && goal.getDeadline().isBefore(LocalDate.now())) {
                log.debug("Цель просрочена");
                goal.setActive(false);
                goalService.update(goal);
                continue;
            }

            BigDecimal contributionValue = calculateGoalContribution(goal, income, remainingAmount, salary); // значение, на которое надо пополнить счёт
//            BigDecimal goalRest = contributionValue.add(goal.getBalance()).subtract(goal.getTargetAmount()); // остаток, появляющийся, если income превышает месячную зп

//            if (goalRest.compareTo(goal.getTargetAmount()) > 0) {
//                rest = rest.add(goalRest);
//                goalContribution = goalContribution.subtract(goalRest);
//            }

            prediction.put(goal.getName(), contributionValue); // на этом этапе завершается вычисление суммы, на которую пополняется счёт цели
            remainingAmount = remainingAmount.subtract(contributionValue);

            ContributionDto contribution = new ContributionDto(goal.getId(), contributionValue, now());
            contributionService.create(contribution);

            goal.setBalance(goal.getBalance().add(contributionValue));
            goalService.update(goal);
        }

        prediction.put("Остаток", remainingAmount);
        log.debug("Результат расчёта: {}", prediction);
        return prediction;
    }

    private BigDecimal calculateGoalContribution(GoalDto goal, BigDecimal income, BigDecimal remainingAmount, BigDecimal salary) {
        return switch (goal.getType()) {
            case FIXED_AMOUNT_WITHOUT_DEADLINE -> calculateFixedAmountWithoutDeadline(goal, salary, income);
            case MONTHLY_PERCENTAGE_WITHOUT_DEADLINE -> calculateMonthlyPercentageWithoutDeadline(goal, income);
            default -> throw new RuntimeException("Not implemented");
        };
    }

    private BigDecimal calculateFixedAmountWithoutDeadline(GoalDto goal, BigDecimal salary, BigDecimal income) {
        var result = goal.getMonthlyAmount().multiply(
                percentOf(income, salary),
                new MathContext(12, HALF_DOWN)
        );
        return result.setScale(2, HALF_DOWN);
    }

    private BigDecimal calculateMonthlyPercentageWithoutDeadline(GoalDto goal, BigDecimal income) {
        var result = income.multiply(
                percentOf(goal.getMonthlyAmount(), new BigDecimal("100")),
                new MathContext(12, HALF_DOWN)
        );
        return result.setScale(2, HALF_DOWN);
    }

}
