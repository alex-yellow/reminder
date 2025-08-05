package com.example.reminder.service;

import com.example.reminder.model.Reminder;
import com.example.reminder.model.User;
import com.example.reminder.repository.ReminderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderSchedulerService {

    private final ReminderRepository reminderRepository;
    private final EmailNotificationService emailService;
    private final TelegramNotificationService telegramService;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void checkReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate currentDate = now.toLocalDate();
        int currentHour = now.getHour();

        List<Reminder> reminders = reminderRepository.findAll();
        List<Reminder> remindersToNotify = reminders.stream()
                .filter(r -> {
                    LocalDateTime dt = r.getRemind();
                    return dt.toLocalDate().equals(currentDate)
                            && dt.getHour() == currentHour;
                })
                .toList();

        for (Reminder reminder : remindersToNotify) {
            User user = reminder.getUser();
            String subject = "Напоминание: " + reminder.getTitle();
            String body = "Описание: " + reminder.getDescription() + "\nВремя: " + reminder.getRemind();

            emailService.sendReminder(user.getEmail(), subject, body);

            if (user.getTelegramId() != null && !user.getTelegramId().isEmpty()) {
                telegramService.sendMessage(user.getTelegramId(), subject + "\n" + body);
            }
        }
    }
}