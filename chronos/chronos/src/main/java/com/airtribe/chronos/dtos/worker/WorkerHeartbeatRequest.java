package com.airtribe.chronos.dtos.worker;


import java.time.Instant;

public record WorkerHeartbeatRequest(
        String workerId,
        Instant lastSeen,
        int runningExecutions
) {}
