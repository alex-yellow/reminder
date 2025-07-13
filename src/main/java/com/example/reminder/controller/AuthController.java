package com.example.reminder.controller;

import com.example.reminder.dto.RegisterRequest;
import com.example.reminder.service.KeycloakAdminClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final KeycloakAdminClient keycloakAdminClient;

    public AuthController(KeycloakAdminClient keycloakAdminClient) {
        this.keycloakAdminClient = keycloakAdminClient;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        keycloakAdminClient.createUser(request);
        return ResponseEntity.status(201).body("User registered successfully");
    }

    @GetMapping("/me")
    public String getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return "Logged in as: " + username;
    }
}