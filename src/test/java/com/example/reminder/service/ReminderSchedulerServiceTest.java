package com.example.reminder.service;

import com.example.reminder.model.Reminder;
import com.example.reminder.model.User;
import com.example.reminder.repository.ReminderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderSchedulerServiceTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private EmailNotificationService emailService;

    @Mock
    private TelegramNotificationService telegramService;

    @InjectMocks
    private ReminderSchedulerService schedulerService;

    @Test
    void checkReminders_ShouldNotifyForMatchingReminders() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setTelegramId("telegram123");

        Reminder reminder1 = new Reminder();
        reminder1.setTitle("Test Reminder");
        reminder1.setDescription("Test Description");
        reminder1.setRemind(LocalDateTime.now());
        reminder1.setUser(user);

        when(reminderRepository.findAll()).thenReturn(List.of(reminder1));

        schedulerService.checkReminders();

        verify(emailService).sendReminder(eq("user@example.com"), anyString(), anyString());
        verify(telegramService).sendMessage(eq("telegram123"), anyString());
    }

    @Test
    void checkReminders_ShouldSkipNonMatchingReminders() {
        Reminder reminder = new Reminder();
        reminder.setRemind(LocalDateTime.now().plusHours(1));
        reminder.setUser(new User());

        when(reminderRepository.findAll()).thenReturn(List.of(reminder));

        schedulerService.checkReminders();

        verify(emailService, never()).sendReminder(any(), any(), any());
        verify(telegramService, never()).sendMessage(any(), any());
    }
}