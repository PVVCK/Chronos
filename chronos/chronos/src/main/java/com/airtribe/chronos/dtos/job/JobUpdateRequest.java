package com.airtribe.chronos.dtos.job;

import java.time.Duration;

/**
 * Payload for updating an existing Job.
 * Allows partial updates (nullable fields).
 */
public record JobUpdateRequest(
        String name,
        String payload,
        Duration timeout,
        Integer maxAttempts,
        String retryPolicyId
) {}