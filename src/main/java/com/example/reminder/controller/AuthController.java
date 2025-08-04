package com.example.reminder.controller;

import com.example.reminder.dto.RegisterRequest;
import com.example.reminder.service.KeycloakAdminClient;
import com.example.reminder.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final KeycloakAdminClient keycloakAdminClient;

    public AuthController(RegistrationService registrationService, KeycloakAdminClient keycloakAdminClient) {
        this.registrationService = registrationService;
        this.keycloakAdminClient = keycloakAdminClient;
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        registrationService.register(request);
        return ResponseEntity.status(201).body("User registered successfully");
    }

    @GetMapping("/me")
    public String getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return "Logged in as: " + username;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String refreshToken) {
        try {
            keycloakAdminClient.logout(refreshToken);
            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Logout failed: " + e.getMessage());
        }
    }
}