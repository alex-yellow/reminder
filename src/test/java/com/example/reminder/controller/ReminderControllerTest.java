package com.example.reminder.controller;

import com.example.reminder.config.SecurityConfig;
import com.example.reminder.controller.ReminderController;
import com.example.reminder.dto.ReminderCreateDto;
import com.example.reminder.dto.ReminderDto;
import com.example.reminder.dto.ReminderMapper;
import com.example.reminder.model.User;
import com.example.reminder.repository.UserRepository;
import com.example.reminder.service.ReminderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

@WebMvcTest(ReminderController.class)
@AutoConfigureMockMvc(addFilters = false) // отключаем security фильтры для тестов
class ReminderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReminderService reminderService;

    @MockBean
    private ReminderMapper reminderMapper;

    @MockBean
    private UserRepository userRepository;

    private ObjectMapper objectMapper;

    private final String BASE_URL = "/api/v1/reminder";

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void shouldReturnCreatedReminder() throws Exception {
        ReminderCreateDto createDto = new ReminderCreateDto("Test title", "Test desc", LocalDateTime.now());
        ReminderDto responseDto = new ReminderDto(1L, "Test title", "Test desc", null);

        Mockito.when(reminderService.create(any(), any())).thenReturn(responseDto);

        mockMvc.perform(post(BASE_URL + "/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());
    }
}