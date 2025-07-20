package com.example.reminder.service;

import com.example.reminder.dto.ReminderCreateDto;
import com.example.reminder.dto.ReminderDto;
import com.example.reminder.dto.ReminderMapper;
import com.example.reminder.model.Reminder;
import com.example.reminder.model.User;
import com.example.reminder.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final ReminderMapper reminderMapper;

    @Override
    public ReminderDto create(ReminderCreateDto dto, User user) {
        Reminder reminder = reminderMapper.toEntity(dto);
        reminder.setUser(user);
        return reminderMapper.toDto(reminderRepository.save(reminder));
    }

    @Override
    public void delete(Long id, User user) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        if (!reminder.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to delete this reminder");
        }
        reminderRepository.delete(reminder);
    }

    @Override
    public ReminderDto update(Long id, ReminderCreateDto dto, User user) {
        Reminder reminder = reminderRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        reminder.setTitle(dto.getTitle());
        reminder.setDescription(dto.getDescription());
        reminder.setRemind(dto.getRemind());

        return reminderMapper.toDto(reminderRepository.save(reminder));
    }

    @Override
    public Page<ReminderDto> list(int page, int size, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("remind").descending());
        return reminderRepository.findAllByUser(user, pageable)
                .map(reminderMapper::toDto);
    }

    @Override
    public List<ReminderDto> search(String title, String description, LocalDateTime dateTime, User user) {
        List<Reminder> reminders;

        if (dateTime == null) {
            reminders = reminderRepository.findByTitleContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndUser(
                    title == null ? "" : title,
                    description == null ? "" : description,
                    user
            );
        } else {
            reminders = reminderRepository.findByTitleContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndRemindAndUser(
                    title == null ? "" : title,
                    description == null ? "" : description,
                    dateTime,
                    user
            );
        }
        return reminderMapper.toDtoList(reminders);
    }

    @Override
    public List<ReminderDto> sort(String by, User user) {
        Sort sort;
        switch (by.toLowerCase()) {
            case "title":
                sort = Sort.by("title");
                break;
            case "date":
            case "time":
                sort = Sort.by("remind");
                break;
            default:
                throw new IllegalArgumentException("Invalid sort parameter: " + by);
        }
        return reminderMapper.toDtoList(reminderRepository.findAllByUser(user, sort));
    }

    @Override
    public List<ReminderDto> filter(LocalDate date, LocalTime time, User user) {
        LocalDateTime start = LocalDateTime.of(date, time != null ? time : LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(date, time != null ? time.plusMinutes(1) : LocalTime.MAX);

        List<Reminder> reminders = reminderRepository.findAllByUserAndRemindBetween(user, start, end);
        return reminderMapper.toDtoList(reminders);
    }
}