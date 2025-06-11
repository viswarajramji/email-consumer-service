package com.example.email.dto;

public record EmailStatsResponse(
        int uniqueEmailCount,
        int uniqueDomainCount
) {}