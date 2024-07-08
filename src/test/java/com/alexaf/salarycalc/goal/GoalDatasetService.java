package com.alexaf.salarycalc.goal;

import com.alexaf.salarycalc.goal.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoalDatasetService {

    private final GoalRepository goalRepository;

}
