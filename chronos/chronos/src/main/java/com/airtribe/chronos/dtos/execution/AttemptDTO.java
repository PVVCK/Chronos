package com.airtribe.chronos.dtos.execution;

import com.airtribe.chronos.enums.AttemptStatus;
import java.time.Instant;

public record AttemptDTO(
        String id,
        String executionId,
        int attemptNo,
        String workerId,
        AttemptStatus status,
        Instant startedAt,
        Instant finishedAt,
        String errorMessage,
        String logsUri
) {}
