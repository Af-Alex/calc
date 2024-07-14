package com.alexaf.salarycalc.contribution;

import com.alexaf.salarycalc.contribution.dto.ContributionDto;
import com.alexaf.salarycalc.contribution.repository.Contribution;
import com.alexaf.salarycalc.contribution.repository.ContributionRepository;
import com.alexaf.salarycalc.goal.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final ContributionMapper contributionMapper;
    private final GoalService goalService;

    public List<Contribution> findByGoalId(UUID goalId) {
        return contributionRepository.findByGoal_Id(goalId);
    }

    public ContributionDto create(@Valid ContributionDto contributionDto) {
        var entity = contributionMapper.createFromDto(contributionDto);
        entity.setGoal(goalService.getById(contributionDto.getGoalId()));

        return contributionMapper.toDto(contributionRepository.save(entity));
    }
}
