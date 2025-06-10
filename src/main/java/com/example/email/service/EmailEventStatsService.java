package com.example.email.service;

import com.example.email.event.EmailEvent;

public interface EmailEventStatsService {

    void processEmailEvent(EmailEvent emailEvent);

    int getUniqueEmailCount();

    int getUniqueDomainCount();

    int getUniqueEmailCount(int lastSeconds);    // time-windowed

    int getUniqueDomainCount(int lastSeconds);   // time-windowed

    void cleanUp(int olderThanSeconds);
}
