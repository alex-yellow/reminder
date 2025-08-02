package com.example.reminder.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailNotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailNotificationService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props);
        mimeMessage = new MimeMessage(session);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendTestEmail_ShouldSendEmail() {
        emailService.sendTestEmail("test@example.com");
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendReminder_ShouldSendFormattedEmail() {
        emailService.sendReminder("user@example.com", "Test Subject", "Test Body");
        verify(mailSender).send(mimeMessage);
    }
}