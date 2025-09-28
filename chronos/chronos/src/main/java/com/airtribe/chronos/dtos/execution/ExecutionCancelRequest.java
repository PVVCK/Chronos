package com.airtribe.chronos.dtos.execution;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExecutionCancelRequest(
         @NotBlank String reason
) {}