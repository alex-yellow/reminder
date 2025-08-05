package com.example.reminder.controller;

import com.example.reminder.dto.PagedResponse;
import com.example.reminder.dto.ReminderCreateDto;
import com.example.reminder.dto.ReminderDto;
import com.example.reminder.model.User;
import com.example.reminder.repository.UserRepository;
import com.example.reminder.service.ReminderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ReminderControllerTest {

    @Mock
    private ReminderService reminderService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReminderController reminderController;

    private Jwt jwt;
    private User user;

    @BeforeEach
    public void setUp() {
        jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("testUser");

        user = new User();
        user.setUsername("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
    }

    @Test
    public void createReminder_ShouldReturnCreatedReminder() {
        ReminderCreateDto createDto = new ReminderCreateDto("Test title", "Test description", LocalDateTime.now());
        ReminderDto expectedDto = new ReminderDto(1L, "Test title", "Test description", LocalDateTime.now());

        when(reminderService.create(createDto, user)).thenReturn(expectedDto);

        ResponseEntity<ReminderDto> response = reminderController.create(createDto, jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(reminderService, times(1)).create(createDto, user);
    }
    @Test
    public void deleteReminder_ShouldReturnNoContent() {
        Long reminderId = 1L;
        ResponseEntity<Void> response = reminderController.delete(reminderId, jwt);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(reminderService, times(1)).delete(reminderId, user);
    }
    @Test
    public void updateReminder_ShouldReturnUpdatedReminder() {
        Long reminderId = 1L;
        ReminderCreateDto updateDto = new ReminderCreateDto("Updated title", "Updated desc", LocalDateTime.now());
        ReminderDto expectedDto = new ReminderDto(reminderId, "Updated title", "Updated desc", LocalDateTime.now());

        when(reminderService.update(reminderId, updateDto, user)).thenReturn(expectedDto);

        ResponseEntity<ReminderDto> response = reminderController.update(reminderId, updateDto, jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(reminderService, times(1)).update(reminderId, updateDto, user);
    }
    @Test
    public void listReminders_ShouldReturnPagedResponse() {
        int page = 0;
        int size = 10;

        List<ReminderDto> reminders = List.of(
                new ReminderDto(1L, "Title 1", "Desc 1", LocalDateTime.now()),
                new ReminderDto(2L, "Title 2", "Desc 2", LocalDateTime.now().plusHours(1))
        );

        Page<ReminderDto> pageResult = new PageImpl<>(reminders);
        when(reminderService.list(page, size, user)).thenReturn(pageResult);


        ResponseEntity<PagedResponse<ReminderDto>> response = reminderController.list(page, size, jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getItems().size());
        assertEquals(2, response.getBody().getTotal());
        assertEquals(0, response.getBody().getCurrent());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(reminderService, times(1)).list(page, size, user);
    }
    @Test
    public void searchReminders_ShouldReturnFilteredResults() {
        String title = "Важная";
        String description = "срочно";
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 31, 23, 59);

        List<ReminderDto> expectedResults = List.of(
                new ReminderDto(1L, "Важная встреча", "Срочно подготовить документы", dateTime),
                new ReminderDto(2L, "Важная задача", "Срочно выполнить", dateTime.plusHours(1))
        );

        when(reminderService.search(title, description, dateTime, user)).thenReturn(expectedResults);

        ResponseEntity<List<ReminderDto>> response = reminderController.search(title, description, dateTime, jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(reminderService, times(1)).search(title, description, dateTime, user);
    }

    @Test
    public void searchReminders_ShouldWorkWithNullParams() {
        List<ReminderDto> expectedResults = List.of(
                new ReminderDto(1L, "Встреча", "Подготовить документы", LocalDateTime.now()),
                new ReminderDto(2L, "Задача", "Выполнить", LocalDateTime.now().plusHours(1))
        );

        when(reminderService.search(null, null, null, user)).thenReturn(expectedResults);

        ResponseEntity<List<ReminderDto>> response = reminderController.search(null, null, null, jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(reminderService, times(1)).search(null, null, null, user);
    }
    @Test
    public void filterReminders_ShouldFilterByDate() {
        LocalDate date = LocalDate.of(2023, 12, 31);
        LocalTime time = null;

        List<ReminderDto> expectedResults = List.of(
                new ReminderDto(1L, "Новый год", "Подготовка", LocalDateTime.of(2023, 12, 31, 20, 0))
        );

        when(reminderService.filter(date, time, user)).thenReturn(expectedResults);

        ResponseEntity<List<ReminderDto>> response = reminderController.filter(date, time, jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(date, response.getBody().get(0).getRemind().toLocalDate());
        verify(reminderService, times(1)).filter(date, time, user);
    }

    @Test
    public void filterReminders_ShouldFilterByTime() {
        LocalDate date = null;
        LocalTime time = LocalTime.of(14, 0);

        List<ReminderDto> expectedResults = List.of(
                new ReminderDto(1L, "Обед", "Перерыв", LocalDateTime.of(2023, 1, 1, 14, 0)),
                new ReminderDto(2L, "Встреча", "С коллегами", LocalDateTime.of(2023, 2, 1, 14, 0))
        );

        when(reminderService.filter(date, time, user)).thenReturn(expectedResults);

        ResponseEntity<List<ReminderDto>> response = reminderController.filter(date, time, jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(time, response.getBody().get(0).getRemind().toLocalTime());
        verify(reminderService, times(1)).filter(date, time, user);
    }

    @Test
    public void filterReminders_ShouldFilterByDateAndTime() {
        LocalDate date = LocalDate.of(2023, 12, 31);
        LocalTime time = LocalTime.of(20, 0);

        List<ReminderDto> expectedResults = List.of(
                new ReminderDto(1L, "Новый год", "Празднование", LocalDateTime.of(2023, 12, 31, 20, 0))
        );

        when(reminderService.filter(date, time, user)).thenReturn(expectedResults);

        ResponseEntity<List<ReminderDto>> response = reminderController.filter(date, time, jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(date, response.getBody().get(0).getRemind().toLocalDate());
        assertEquals(time, response.getBody().get(0).getRemind().toLocalTime());
        verify(reminderService, times(1)).filter(date, time, user);
    }
    @Test
    public void sortReminders_ShouldSortByTitle() {
        String sortBy = "title";

        List<ReminderDto> expectedResults = List.of(
                new ReminderDto(1L, "A задача", "Описание 1", LocalDateTime.now()),
                new ReminderDto(2L, "B встреча", "Описание 2", LocalDateTime.now().plusHours(1))
        );

        when(reminderService.sort(sortBy, user)).thenReturn(expectedResults);

        ResponseEntity<List<ReminderDto>> response = reminderController.sort(sortBy, jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("A задача", response.getBody().get(0).getTitle());
        verify(reminderService, times(1)).sort(sortBy, user);
    }

    @Test
    public void sortReminders_ShouldSortByDate() {
        String sortBy = "date";

        LocalDateTime now = LocalDateTime.now();
        List<ReminderDto> expectedResults = List.of(
                new ReminderDto(1L, "Задача", "Описание 1", now),
                new ReminderDto(2L, "Встреча", "Описание 2", now.plusHours(1))
        );

        when(reminderService.sort(sortBy, user)).thenReturn(expectedResults);

        ResponseEntity<List<ReminderDto>> response = reminderController.sort(sortBy, jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertThat(response.getBody().get(0).getRemind())
                .isBefore(response.getBody().get(1).getRemind());
        verify(reminderService, times(1)).sort(sortBy, user);
    }
}