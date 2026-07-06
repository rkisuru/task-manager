package com.taskflow.userservice.controller;

import com.taskflow.userservice.dto.UserResponse;
import com.taskflow.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Get the currently authenticated user's profile.
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestAttribute("userId") String userId) {
        UserResponse response = userService.getUserById(UUID.fromString(userId));
        return ResponseEntity.ok(response);
    }

    /**
     * Get a user by their ID.
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }
}
