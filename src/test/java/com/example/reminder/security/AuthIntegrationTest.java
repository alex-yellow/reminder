package com.example.reminder.security;

import com.example.reminder.dto.RegisterRequest;
import com.example.reminder.model.User;
import com.example.reminder.repository.UserRepository;
import com.example.reminder.service.KeycloakAdminClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KeycloakAdminClient keycloakAdminClient;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testSuccessfulUserRegistration() throws Exception {
        doNothing().when(keycloakAdminClient).createUser(any(RegisterRequest.class));
        when(userRepository.save(any(User.class))).thenReturn(new User());

        String requestBody = """
                {
                    "username": "testuser",
                    "email": "test@example.com",
                    "password": "testpassword"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }


    @Test
    @WithMockUser
    public void testSuccessfulLogout() throws Exception {
        doNothing().when(keycloakAdminClient).logout("valid-token");

        mockMvc.perform(post("/api/v1/auth/logout")
                        .param("refreshToken", "valid-token")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful"));
    }

    @Test
    @WithMockUser
    public void testLogoutWithInvalidToken() throws Exception {
        doThrow(new RuntimeException("Invalid token"))
                .when(keycloakAdminClient).logout("invalid-token");

        mockMvc.perform(post("/api/v1/auth/logout")
                        .param("refreshToken", "invalid-token")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Logout failed")));
    }
}