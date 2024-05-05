package com.alexaf.salarycalc.user;

import com.alexaf.salarycalc.telegram.ChatState;
import com.alexaf.salarycalc.user.dto.TgUser;
import com.alexaf.salarycalc.user.repository.TgUserEntity;
import com.alexaf.salarycalc.user.repository.TgUserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class TgUserService {

    private final TgUserRepository userRepository;
    private final TgUserMapper userMapper;

    public void updateChatState(Long userId, ChatState newState) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        user.setChatState(newState);
        userRepository.save(user);
    }

    public TgUser get(Long userId) {
        return userRepository.findById(userId).map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }

    public Optional<TgUser> find(Long userId) {
        return userRepository.findById(userId).map(userMapper::toDto);
    }

    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    public TgUser save(@Valid @NotNull TgUser user) {
        TgUserEntity entity;
        if (!existsById(user.getId()))
            entity = userRepository.save(userMapper.toEntity(user));
        else {
            var oldUser = userRepository.findById(user.getId()).orElseThrow();
            entity = userRepository.save(userMapper.partialUpdate(user, oldUser));
        }

        return userMapper.toDto(entity);

    }
}
