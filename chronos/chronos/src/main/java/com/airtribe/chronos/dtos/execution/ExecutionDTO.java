package com.airtribe.chronos.dtos.execution;

import com.airtribe.chronos.enums.ExecutionStatus;
import java.time.Instant;

public record ExecutionDTO(
        String id,
        String scheduleId,
        String jobId,
        ExecutionStatus status,
        Instant dueAt,
        Instant enqueuedAt,
        Instant startedAt,
        Instant finishedAt,
        int attemptCount,
        String correlationId
) {}
