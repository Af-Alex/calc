package com.alexaf.salarycalc.contribution;

import com.alexaf.salarycalc.contribution.repository.Contribution;
import com.alexaf.salarycalc.contribution.repository.ContributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionRepository contributionRepository;

    public List<Contribution> findByGoalId(UUID goalId) {
        return contributionRepository.findByGoal_Id(goalId);
    }

    public Contribution save(Contribution contribution) {
        return contributionRepository.save(contribution);
    }
}
