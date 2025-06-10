package com.example.email.scheduler.impl;
import com.example.email.scheduler.CleanupSchedulerService;
import com.example.email.service.EmailEventStatsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CleanupSchedulerServiceImpl implements CleanupSchedulerService {

    private static final int CLEANUP_THRESHOLD_SECONDS = 30 * 60; // 30 minutes

    private final EmailEventStatsService emailEventStatsService;

    public CleanupSchedulerServiceImpl(EmailEventStatsService emailEventStatsService) {
        this.emailEventStatsService = emailEventStatsService;
    }

    @Override
    @Scheduled(fixedRate = 60_000)
    public void cleanupOldEntries() {
        System.out.println("trigger calls");
        emailEventStatsService.cleanUp(CLEANUP_THRESHOLD_SECONDS);
    }
}