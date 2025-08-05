package com.example.reminder.controller;

import com.example.reminder.service.EmailNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailTestControllerTest {

    @Mock
    private EmailNotificationService emailService;

    @InjectMocks
    private MailTestController mailTestController;

    @Test
    void sendTestMail_ShouldReturnOk() {
        String testEmail = "test@example.com";
        doNothing().when(emailService).sendTestEmail(testEmail);

        ResponseEntity<String> response = mailTestController.sendTestMail(testEmail);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email sent", response.getBody());
        verify(emailService).sendTestEmail(testEmail);
    }

    @Test
    void sendTestMail_ShouldHandleServiceException() {
        String testEmail = "test@example.com";
        String errorMessage = "SMTP error";
        doThrow(new RuntimeException(errorMessage))
                .when(emailService)
                .sendTestEmail(testEmail);

        ResponseEntity<String> response = mailTestController.sendTestMail(testEmail);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to send email: " + errorMessage, response.getBody());
        verify(emailService).sendTestEmail(testEmail);
    }
}