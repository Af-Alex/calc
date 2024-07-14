package com.alexaf.salarycalc.contribution.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContributionRepository extends JpaRepository<Contribution, UUID> {
    List<Contribution> findByGoal_Id(UUID id);
}