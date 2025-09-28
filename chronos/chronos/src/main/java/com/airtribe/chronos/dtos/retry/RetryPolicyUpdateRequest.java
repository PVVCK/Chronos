package com.airtribe.chronos.dtos.retry;

import com.airtribe.chronos.enums.RetryStrategyKey;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RetryPolicyUpdateRequest(
        @NotNull RetryStrategyKey strategy,
        @Min(0) Long backoffInitialMs,
        @DecimalMin(value = "1.0", inclusive = true) Double backoffFactor,
        @Min(1) Integer maxAttempts,
        @Min(0) Long jitterMs
) {}

