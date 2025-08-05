package com.example.reminder.service;

import com.example.reminder.dto.ReminderCreateDto;
import com.example.reminder.dto.ReminderDto;
import com.example.reminder.model.User;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface ReminderService {
    ReminderDto create(ReminderCreateDto dto, User user);
    void delete(Long id, User user);
    ReminderDto update(Long id, ReminderCreateDto dto, User user);
    Page<ReminderDto> list(int page, int size, User user);
    List<ReminderDto> search(String title, String description, LocalDateTime dateTime, User user);
    List<ReminderDto> sort(String by, User user);
    List<ReminderDto> filter(LocalDate date, LocalTime time, User user);
}