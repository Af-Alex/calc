package com.alexaf.salarycalc.user.controller;

import com.alexaf.salarycalc.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserResponse get(@PathVariable UUID userId) {
        return new UserResponse(userService.get(userId));
    }

    @GetMapping("/unique-filter")
    public UserResponse getByUniqueFilter(@ParameterObject @ModelAttribute @Valid UniqueFilter filter) {
        return new UserResponse(userService.findByFilter(filter));
    }

    @PreAuthorize("hasAuthority('admin:delete')")
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }
}
