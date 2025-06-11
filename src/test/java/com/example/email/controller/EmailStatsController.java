package com.example.email.controller;

import com.example.email.advice.GlobalExceptionHandler;
import com.example.email.dto.EmailStatsResponse;
import com.example.email.service.EmailEventStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmailStatsController.class)
class EmailStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailEventStatsService statsService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new EmailStatsController(statsService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetEmailStatistics() throws Exception {
        when(statsService.getUniqueEmailCount()).thenReturn(10);
        when(statsService.getUniqueDomainCount()).thenReturn(5);

        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueEmailCount").value(10))
                .andExpect(jsonPath("$.uniqueDomainCount").value(5));
    }

    @Test
    void testGetRecentStatsWithValidInput() throws Exception {
        when(statsService.getUniqueEmailCount(60)).thenReturn(7);
        when(statsService.getUniqueDomainCount(60)).thenReturn(3);

        mockMvc.perform(get("/api/stats/recent").param("lastSeconds", "60"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueEmailCount").value(7))
                .andExpect(jsonPath("$.uniqueDomainCount").value(3));
    }

    @Test
    void testGetRecentStatsWithInvalidInput() throws Exception {
        mockMvc.perform(get("/api/stats/recent").param("lastSeconds", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_SECONDS"))
                .andExpect(jsonPath("$.errorDescription").value("lastSeconds must be greater than 0"));
    }
}
