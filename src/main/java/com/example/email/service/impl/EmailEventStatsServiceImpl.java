package com.example.email.service.impl;

import com.example.email.event.EmailEvent;
import com.example.email.service.EmailEventStatsService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailEventStatsServiceImpl implements EmailEventStatsService {

    private final Map<String, Instant> emailTimestamps = new ConcurrentHashMap<>();
    private final Map<String, Instant> domainTimestamps = new ConcurrentHashMap<>();

    @Override
    public void processEmailEvent(EmailEvent emailEvent) {
        if (emailEvent == null || emailEvent.email() == null || emailEvent.eventTime() == null) return;

        String email = emailEvent.email().toLowerCase();
        Instant eventTime = emailEvent.eventTime();
        emailTimestamps.put(email, eventTime);

        int atIndex = email.indexOf("@");
        if (atIndex > 0 && atIndex < email.length() - 1) {
            String domain = email.substring(atIndex + 1);
            domainTimestamps.put(domain, eventTime);
        }
    }

    @Override
    public int getUniqueEmailCount() {
        return emailTimestamps.size();
    }

    @Override
    public int getUniqueDomainCount() {
        return domainTimestamps.size();
    }

    @Override
    public int getUniqueEmailCount(int lastSeconds) {
        Instant cutoff = Instant.now().minusSeconds(lastSeconds);
        return (int) emailTimestamps.values().stream()
                .filter(ts -> ts.isAfter(cutoff))
                .count();
    }

    @Override
    public int getUniqueDomainCount(int lastSeconds) {
        Instant cutoff = Instant.now().minusSeconds(lastSeconds);
        return (int) domainTimestamps.values().stream()
                .filter(ts -> ts.isAfter(cutoff))
                .count();
    }

    @Override
    public void cleanUp(int olderThanSeconds) {
        Instant cutoff = Instant.now().minusSeconds(olderThanSeconds);
        emailTimestamps.entrySet().removeIf(e -> e.getValue().isBefore(cutoff));
        domainTimestamps.entrySet().removeIf(e -> e.getValue().isBefore(cutoff));
    }

}
