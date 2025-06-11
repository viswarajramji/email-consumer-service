package com.example.email.service;


import com.example.email.event.EmailEvent;
import com.example.email.service.EmailEventStatsService;
import com.example.email.service.impl.EmailEventStatsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class EmailEventStatsServiceImplTest {

    private EmailEventStatsService service;

    @BeforeEach
    void setUp() {
        service = new EmailEventStatsServiceImpl();
    }

    @Test
    void testProcessEmailEventStoresEmailAndDomain() {
        String email = "test@example.com";
        Instant now = Instant.now();

        EmailEvent event = new EmailEvent(email, now);
        service.processEmailEvent(event);

        assertEquals(1, service.getUniqueEmailCount());
        assertEquals(1, service.getUniqueDomainCount());
    }

    @Test
    void testProcessEmailEventIgnoresNulls() {
        service.processEmailEvent(null);
        service.processEmailEvent(new EmailEvent(null, Instant.now()));
        service.processEmailEvent(new EmailEvent("test@example.com", null));

        assertEquals(0, service.getUniqueEmailCount());
        assertEquals(0, service.getUniqueDomainCount());
    }

    @Test
    void testUniqueCountWithTimeFilter() throws InterruptedException {
        EmailEvent event1 = new EmailEvent("user1@test.com", Instant.now());
        service.processEmailEvent(event1);

        Thread.sleep(1000); // Sleep to ensure time difference

        EmailEvent event2 = new EmailEvent("user2@test.com", Instant.now());
        service.processEmailEvent(event2);

        // Both emails within last 5 seconds
        assertEquals(2, service.getUniqueEmailCount(5));
        assertEquals(1, service.getUniqueDomainCount(5)); // both same domain

        // Only second email within last 1 second
        assertEquals(1, service.getUniqueEmailCount(1));
    }

    @Test
    void testCleanUpRemovesOldEntries() throws InterruptedException {
        service.processEmailEvent(new EmailEvent("old@example.com", Instant.now().minusSeconds(10)));
        service.processEmailEvent(new EmailEvent("new@example.com", Instant.now()));

        service.cleanUp(5); // Remove entries older than 5 seconds

        assertEquals(1, service.getUniqueEmailCount());
        assertEquals(1, service.getUniqueDomainCount());
    }
}
