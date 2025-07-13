package com.example.reminder.service;

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

    private RestTemplate restTemplate = new RestTemplate();

    private String getAdminAccessToken() {
        String url = keycloakServerUrl + "/realms/master/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        Map<String, Object> body = response.getBody();

        if (body == null || !body.containsKey("access_token")) {
            throw new RuntimeException("Cannot obtain admin access token from Keycloak");
        }

        return (String) body.get("access_token");
    }

    public void createUser(RegisterRequest registerRequest) {
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users";

        String adminToken = getAdminAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("username", registerRequest.getUsername());
        userPayload.put("email", registerRequest.getEmail());
        userPayload.put("enabled", true);
        userPayload.put("credentials", List.of(Map.of(
                "type", "password",
                "value", registerRequest.getPassword(),
                "temporary", false
        )));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userPayload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to create user in Keycloak: " + response.getBody());
        }
    }
}