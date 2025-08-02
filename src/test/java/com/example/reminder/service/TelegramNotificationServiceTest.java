package com.example.reminder.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramNotificationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TelegramNotificationService telegramService;

    @Test
    void sendMessage_ShouldCallTelegramApi() {
        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(
                RestTemplate.class,
                (mock, context) -> {
                    when(mock.getForObject(anyString(), eq(String.class)))
                            .thenReturn("{\"ok\":true}");
                })) {

            TelegramNotificationService service = new TelegramNotificationService();
            ReflectionTestUtils.setField(service, "apiUrl",
                    "https://api.telegram.org/botTEST_TOKEN/");

            service.sendMessage("12345", "Test message");

            RestTemplate mockTemplate = mocked.constructed().get(0);
            verify(mockTemplate).getForObject(
                    contains("sendMessage?chat_id=12345"),
                    eq(String.class)
            );
        }
    }
}