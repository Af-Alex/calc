package com.alexaf.salarycalc.user;

import com.alexaf.salarycalc.user.controller.UniqueFilter;
import com.alexaf.salarycalc.user.dto.Role;
import com.alexaf.salarycalc.user.dto.User;
import com.alexaf.salarycalc.user.entity.UserEntity;
import com.alexaf.salarycalc.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

import static java.lang.String.format;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UUID create(String username, String password, Role role, Long telegramId) {
        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException(format("Username [%s] is already taken", username));

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);
        user.setTelegramId(telegramId);
        return userRepository.save(user).getId();
    }

    public User update(@Valid User user) {
        User existing = get(user.getId());
        userMapper.partialUpdate(existing, user);
        if (!existing.getPassword().equals(user.getPassword()) && user.getPassword() != null) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userMapper.toDto(userRepository.save(userMapper.toEntity(user)));
    }


    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    public User get(UUID userId) {
        return userRepository.findById(userId).map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(format("User [%s] not found", userId)));
    }

    public User findByFilter(UniqueFilter filter) {
        return userRepository.findByUsername(filter.username())
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(format("User [%s] not found", filter.username())));
    }

    /**
     *
     * @param chatId ID пользователя в телеграмме
     */
    public boolean existsByTelegramId(long chatId) {
        return userRepository.existsByTelegramId(chatId);
    }
}
