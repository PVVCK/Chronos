package com.airtribe.chronos.dtos.worker;


import java.time.Instant;

public record WorkerDTO(
        String id,
        String hostname,
        String ipAddress,
        Instant registeredAt,
        Instant lastHeartbeat,
        boolean active
) {}