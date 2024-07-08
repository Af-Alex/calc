package com.alexaf.salarycalc.goal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByUser_Id(UUID id);

    List<Goal> findByActiveTrueAndUser_IdOrderByPriorityAsc(UUID id);

}