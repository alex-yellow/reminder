package com.example.reminder.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramNotificationService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        apiUrl = "https://api.telegram.org/bot" + botToken + "/";
    }

    public void sendMessage(String chatId, String text) {
        String url = apiUrl + "sendMessage?chat_id=" + chatId + "&text=" + text;
        restTemplate.getForObject(url, String.class);
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @PostConstruct
    public void printChatId() {
        System.out.println("📬 Checking Telegram updates...");

        String url = apiUrl + "getUpdates";
        String response = restTemplate.getForObject(url, String.class);
        System.out.println("📨 Response from Telegram: " + response);
    }
}