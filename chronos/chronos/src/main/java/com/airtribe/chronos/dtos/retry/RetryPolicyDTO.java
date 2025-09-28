package com.airtribe.chronos.dtos.retry;

import com.airtribe.chronos.enums.RetryStrategyKey;

public record RetryPolicyDTO(
        String id,
        RetryStrategyKey strategy,
        long backoffInitialMs,
        double backoffFactor,
        int maxAttempts,
        long jitterMs
) {}