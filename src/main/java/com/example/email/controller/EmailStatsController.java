package com.example.email.controller;


import com.example.email.service.EmailEventStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class EmailStatsController {
//TODO: convert to DTO
    private final EmailEventStatsService statsService;

    public EmailStatsController(EmailEventStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public Map<String, Integer> getEmailStatistics() {
        return Map.of(
                "uniqueEmailCount", statsService.getUniqueEmailCount(),
                "uniqueDomainCount", statsService.getUniqueDomainCount()
        );
    }

    @GetMapping("/recent")
    public Map<String, Integer> getRecentStats(@RequestParam(name = "lastSeconds") int lastSeconds) {
        return Map.of(
                "uniqueEmailCount", statsService.getUniqueEmailCount(lastSeconds),
                "uniqueDomainCount", statsService.getUniqueDomainCount(lastSeconds)
        );
    }
}