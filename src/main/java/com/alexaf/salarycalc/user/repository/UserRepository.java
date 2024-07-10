package com.alexaf.salarycalc.user.repository;

import com.alexaf.salarycalc.telegram.statics.ChatState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {


    Optional<User> findByTelegramId(Long telegramId);

    boolean existsByTelegramId(Long telegramId);

    @Transactional
    @Modifying
    @Query("update User u set u.chatState = ?2 where u.id = ?1")
    void updateChatState(UUID userId, ChatState chatState);

    @Transactional
    @Modifying
    @Query("update User u set u.chatState = ?2 where u.telegramId = ?1")
    int updateChatState(Long telegramId, ChatState chatState);
}