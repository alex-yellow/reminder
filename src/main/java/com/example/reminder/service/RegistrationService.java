package com.example.reminder.service;

import com.example.reminder.dto.RegisterRequest;
import com.example.reminder.model.User;
import com.example.reminder.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final KeycloakAdminClient keycloakAdminClient;
    private final UserRepository userRepository;

    public RegistrationService(KeycloakAdminClient keycloakAdminClient, UserRepository userRepository) {
        this.keycloakAdminClient = keycloakAdminClient;
        this.userRepository = userRepository;
    }

    public void register(RegisterRequest request) {
        keycloakAdminClient.createUser(request);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());

        userRepository.save(user);
    }
}
