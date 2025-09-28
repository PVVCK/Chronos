package com.airtribe.chronos.dtos.job;


import com.airtribe.chronos.enums.JobType;
import jakarta.validation.constraints.*;

import java.time.Duration;

/**
 * Payload for creating a new Job.
 */
public record JobCreateRequest(
        @NotBlank String tenantId,
        @NotBlank String name,
        @NotNull JobType type,
        @NotBlank String payload,
        @NotNull Duration timeout,
        @Min(1) int maxAttempts,
        String retryPolicyId,
        String ownerUserId
) {}