package com.alexaf.salarycalc.salary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SalaryRepository extends JpaRepository<Salary, UUID> {
    Salary findFirstByUser_IdOrderByEffectiveDateDesc(UUID id);
}