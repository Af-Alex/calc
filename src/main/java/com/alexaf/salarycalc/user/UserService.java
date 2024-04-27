package com.alexaf.salarycalc.user;

import com.alexaf.salarycalc.user.controller.UniqueFilter;
import com.alexaf.salarycalc.user.dto.Role;
import com.alexaf.salarycalc.user.dto.User;
import com.alexaf.salarycalc.user.entity.UserEntity;
import com.alexaf.salarycalc.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.lang.String.format;
import static java.lang.StringTemplate.STR;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UUID create(String username, String password, Role role) {
        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException(STR."Username \{username} is already taken");

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);
        return userRepository.save(user).getId();
    }


    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    public User get(UUID userId) {
        return userRepository.findById(userId).map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(format("User [%s] not found", userId)));
    }

    public User findByUniqueFilter(UniqueFilter filter) {
        return userRepository.findByUsername(filter.username())
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(format("User [%s] not found", filter.username())));
    }
}
