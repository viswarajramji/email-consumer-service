package com.example.email.controller;


import com.example.email.dto.EmailStatsResponse;
import com.example.email.service.EmailEventStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
@RequestMapping("/api/stats")
public class EmailStatsController {

    private final EmailEventStatsService statsService;

    public EmailStatsController(EmailEventStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public ResponseEntity<EmailStatsResponse> getEmailStatistics() {
        EmailStatsResponse response = new EmailStatsResponse(
                statsService.getUniqueEmailCount(),
                statsService.getUniqueDomainCount()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent")
    public ResponseEntity<EmailStatsResponse> getRecentStats(@RequestParam(name = "lastSeconds") int lastSeconds) {
        EmailStatsResponse response = new EmailStatsResponse(
                statsService.getUniqueEmailCount(lastSeconds),
                statsService.getUniqueDomainCount(lastSeconds)
        );
        return ResponseEntity.ok(response);
    }
}