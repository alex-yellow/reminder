package com.example.reminder.controller;

import com.example.reminder.dto.PagedResponse;
import com.example.reminder.dto.ReminderCreateDto;
import com.example.reminder.dto.ReminderDto;
import com.example.reminder.dto.ReminderMapper;
import com.example.reminder.model.User;
import com.example.reminder.repository.UserRepository;
import com.example.reminder.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@SecurityRequirement(name = "bearerAuth") // или на методах
@RestController
@RequestMapping("/api/v1/reminder")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;
    private final ReminderMapper reminderMapper;
    private final UserRepository userRepository;

    @Operation(summary = "Создать напоминание")
    @PostMapping("/create")
    public ResponseEntity<ReminderDto> create(@RequestBody ReminderCreateDto dto, @AuthenticationPrincipal Jwt principal) {
        User user = getUserFromPrincipal(principal);
        ReminderDto created = reminderService.create(dto, user);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Удалить напоминание")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt principal) {
        User user = getUserFromPrincipal(principal);
        reminderService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Обновить напоминание")
    @PutMapping("/update/{id}")
    public ResponseEntity<ReminderDto> update(@PathVariable Long id, @RequestBody ReminderCreateDto dto, @AuthenticationPrincipal Jwt principal) {
        User user = getUserFromPrincipal(principal);
        ReminderDto updated = reminderService.update(id, dto, user);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Список всех напоминаний")
    @GetMapping("/list")
    public ResponseEntity<PagedResponse<ReminderDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal Jwt principal) {

        User user = getUserFromPrincipal(principal);
        Page<ReminderDto> result = reminderService.list(page, size, user);

        return ResponseEntity.ok(new PagedResponse<>(
                result.getContent(),
                result.getTotalElements(),
                result.getNumber()
        ));
    }

    @Operation(summary = "Поиск напоминаний")
    @GetMapping("/search")
    public ResponseEntity<List<ReminderDto>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @AuthenticationPrincipal Jwt principal) {

        User user = getUserFromPrincipal(principal);
        List<ReminderDto> result = reminderService.search(title, description, dateTime, user);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Сортировка напоминаний")
    @GetMapping("/sort")
    public ResponseEntity<List<ReminderDto>> sort(@RequestParam String by, @AuthenticationPrincipal Jwt principal) {
        User user = getUserFromPrincipal(principal);
        List<ReminderDto> sorted = reminderService.sort(by, user);
        return ResponseEntity.ok(sorted);
    }

    @Operation(summary = "Фильтрация напоминаний")
    @GetMapping("/filter")
    public ResponseEntity<List<ReminderDto>> filter(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @AuthenticationPrincipal Jwt principal) {

        User user = getUserFromPrincipal(principal);
        List<ReminderDto> filtered = reminderService.filter(date, time, user);
        return ResponseEntity.ok(filtered);
    }

    private User getUserFromPrincipal(Jwt principal) {
        String username = principal.getClaimAsString("preferred_username");
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}