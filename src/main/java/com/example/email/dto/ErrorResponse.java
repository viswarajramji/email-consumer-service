package com.example.email.dto;

import java.time.Instant;

public record ErrorResponse(
        String errorCode,
        String errorDescription,
        Instant timestamp
) {}