package com.example.reminder.repository;

import com.example.reminder.model.Reminder;
import com.example.reminder.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReminderRepositoryTest {

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Сохранение и поиск напоминаний пользователя")
    void testFindByUser() {
        User user = new User();
        user.setUsername("testuser1");
        user.setPassword("password1");
        userRepository.save(user);

        Reminder reminder = new Reminder();
        reminder.setTitle("Test reminder");
        reminder.setRemind(LocalDateTime.now().plusDays(1));
        reminder.setUser(user);

        reminderRepository.save(reminder);

        List<Reminder> reminders = reminderRepository.findByUser(user);

        assertThat(reminders).hasSize(1);
        assertThat(reminders.get(0).getTitle()).isEqualTo("Test reminder");
    }
}