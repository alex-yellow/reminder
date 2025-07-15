package com.example.reminder.security;

import com.example.reminder.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/auth";
    }

    @Test
    void registerLoginAndAccessProtectedEndpoint() {
        // 1. Регистрация нового пользователя
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser1");
        registerRequest.setEmail("testuser1@example.com");
        registerRequest.setPassword("test1234");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> registerEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<String> registerResponse = restTemplate.postForEntity(
                baseUrl() + "/register",
                registerEntity,
                String.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // 2. Получение токена для Keycloak
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String tokenUrl = "http://localhost:8081/realms/reminder/protocol/openid-connect/token";

        String body = "grant_type=password"
                + "&client_id=reminder-api"
                + "&client_secret=K1gHFMs8m6OSsZBtYfDmxOTclGjwcfkG"
                + "&username=testuser1"
                + "&password=test1234";

        HttpEntity<String> tokenEntity = new HttpEntity<>(body, tokenHeaders);

        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenEntity, String.class);

        assertThat(tokenResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(tokenResponse.getBody()).contains("access_token");

        // 3. Получение токена
        String accessToken = tokenResponse.getBody().split("\"access_token\":\"")[1].split("\"")[0];

        // 4. Проверка защищенного эндпойнта
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(accessToken);

        HttpEntity<Void> authEntity = new HttpEntity<>(authHeaders);

        ResponseEntity<String> meResponse = restTemplate.exchange(
                baseUrl() + "/me",
                HttpMethod.GET,
                authEntity,
                String.class
        );

        assertThat(meResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(meResponse.getBody()).contains("testuser");
    }
}