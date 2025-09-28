package com.airtribe.chronos.dtos.job;

import com.airtribe.chronos.enums.JobType;
import jakarta.validation.constraints.*;

import java.time.Duration;
import java.time.Instant;

/**
 * Full read model of a Job (used in API responses).
 */
public record JobDTO(
        String id,
        String tenantId,
        String name,
        JobType type,
        String payload,
        Duration timeout,
        int maxAttempts,
        String retryPolicyId,
        String ownerUserId,
        Instant createdAt,
        Instant updatedAt
) {}
