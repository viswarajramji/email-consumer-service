package com.example.email.event;


import java.time.Instant;

public record EmailEvent(String email, Instant eventTime) { }
