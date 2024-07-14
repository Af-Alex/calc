package com.alexaf.salarycalc.user;

import com.alexaf.salarycalc.exception.EntityNotFoundException;
import com.alexaf.salarycalc.telegram.statics.ChatState;
import com.alexaf.salarycalc.user.repository.User;
import com.alexaf.salarycalc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public User getById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(User.class, id));
    }

    public Optional<UserDto> findByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId).map(userMapper::toDto);
    }

    private User create(User user) {
        return userRepository.save(user);
    }

    public UserDto create(UserDto userDto) {
        var entity = userMapper.toEntity(userDto);
        return userMapper.toDto(create(entity));
    }

    public boolean existsByTelegramId(Long userId) {
        return userRepository.existsByTelegramId(userId);
    }

    public UserDto getByTelegramId(Long userId) {
        return userRepository.findByTelegramId(userId).map(userMapper::toDto).orElseThrow(() -> new EntityNotFoundException(User.class, userId));
    }

    public void updateChatState(UUID userId, ChatState newState) {
        userRepository.updateChatState(userId, newState);
    }

    public void updateChatState(Long userId, ChatState newState) {
        userRepository.updateChatState(userId, newState);
    }

}
