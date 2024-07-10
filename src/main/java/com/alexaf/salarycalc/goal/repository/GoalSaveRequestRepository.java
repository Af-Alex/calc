package com.alexaf.salarycalc.goal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalSaveRequestRepository extends JpaRepository<GoalSaveRequest, UUID> {
    Optional<GoalSaveRequest> findByUser_Id(UUID id);

    boolean existsByUser_Id(UUID id);
}