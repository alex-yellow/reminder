package com.example.reminder.service;

import com.example.reminder.config.KeycloakConstants;
import com.example.reminder.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class KeycloakAdminClient {

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getAdminAccessToken() {
        String url = keycloakServerUrl + KeycloakConstants.TOKEN_PATH;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(KeycloakConstants.GRANT_TYPE, KeycloakConstants.GRANT_TYPE_CLIENT_CREDENTIALS);
        params.add(KeycloakConstants.CLIENT_ID, clientId);
        params.add(KeycloakConstants.CLIENT_SECRET, clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        Map<String, Object> body = response.getBody();

        if (body == null || !body.containsKey(KeycloakConstants.ACCESS_TOKEN)) {
            throw new RuntimeException("Cannot obtain admin access token from Keycloak");
        }

        return (String) body.get(KeycloakConstants.ACCESS_TOKEN);
    }

    public void createUser(RegisterRequest registerRequest) {
        String url = keycloakServerUrl + String.format(KeycloakConstants.USERS_PATH_TEMPLATE, realm);
        String adminToken = getAdminAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("username", registerRequest.getUsername());
        userPayload.put("email", registerRequest.getEmail());
        userPayload.put("enabled", true);
        userPayload.put("credentials", List.of(Map.of(
                KeycloakConstants.CREDENTIAL_TYPE, KeycloakConstants.CREDENTIAL_PASSWORD,
                KeycloakConstants.CREDENTIAL_VALUE, registerRequest.getPassword(),
                KeycloakConstants.CREDENTIAL_TEMPORARY, false
        )));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userPayload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to create user in Keycloak: " + response.getBody());
        }
    }

    public void logout(String refreshToken) {
        String url = keycloakServerUrl + String.format(KeycloakConstants.LOGOUT_PATH_TEMPLATE, realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(KeycloakConstants.CLIENT_ID, clientId);
        params.add(KeycloakConstants.CLIENT_SECRET, clientSecret);
        params.add(KeycloakConstants.REFRESH_TOKEN, refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to logout from Keycloak: " + response.getBody());
        }
    }
}