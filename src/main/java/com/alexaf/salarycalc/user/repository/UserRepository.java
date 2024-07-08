package com.alexaf.salarycalc.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    User findByEmail(String email);

    Optional<User> findByTelegramId(Long telegramId);

    boolean existsByTelegramId(Long telegramId);
}