package com.airtribe.chronos.dtos.retry;

import com.airtribe.chronos.enums.RetryStrategyKey;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

public record RetryPolicyCreateRequest(
        @NotNull RetryStrategyKey strategy,
        @Min(0) long backoffInitialMs,
        @DecimalMin(value = "1.0", inclusive = true) double backoffFactor,
        @Min(1) int maxAttempts,
        @Min(0) long jitterMs
) {}
