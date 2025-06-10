package com.example.email.consumer.impl;

import com.example.email.consumer.EventConsumer;
import com.example.email.event.EmailEvent;
import com.example.email.service.EmailEventStatsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class KafkaEventConsumer implements EventConsumer {

    private final ObjectMapper objectMapper;
    private final EmailEventStatsService statsService;
    private static final Logger log = LoggerFactory.getLogger(KafkaEventConsumer.class);

    public KafkaEventConsumer(ObjectMapper objectMapper, EmailEventStatsService statsService) {
        this.objectMapper = objectMapper;
        this.statsService = statsService;
    }

    @Override
    @KafkaListener(topics = "email-events", groupId = "email-stats-group")
    public void consumer(String message) {
        try {
            EmailEvent event = objectMapper.readValue(message, EmailEvent.class);
            log.info("Consumed event: {}", event);
            statsService.processEmailEvent(event);
        } catch (Exception e) {
            log.error("Failed to parse message: {}", message, e);
        }
    }
}
