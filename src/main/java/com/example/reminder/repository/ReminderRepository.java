package com.example.reminder.repository;

import com.example.reminder.model.Reminder;
import com.example.reminder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long>, JpaSpecificationExecutor<Reminder> {

    List<Reminder> findByUser(User user);

    List<Reminder> findByRemindBeforeAndUser(LocalDateTime time, User user);
}