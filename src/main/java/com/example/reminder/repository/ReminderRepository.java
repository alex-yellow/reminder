package com.example.reminder.repository;

import com.example.reminder.model.Reminder;
import com.example.reminder.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Long>, JpaSpecificationExecutor<Reminder> {

    List<Reminder> findByUser(User user);

    Optional<Reminder> findByIdAndUser(Long id, User user);

    Page<Reminder> findAllByUser(User user, Pageable pageable);

    List<Reminder> findAllByUser(User user, Sort sort);

    List<Reminder> findByTitleContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndRemindAndUser(
            String title, String description, LocalDateTime remind, User user);

    List<Reminder> findByTitleContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndUser(
            String title, String description, User user);

    List<Reminder> findAllByUserAndRemindBetween(User user, LocalDateTime start, LocalDateTime end);
}