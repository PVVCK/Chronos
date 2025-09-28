package com.airtribe.chronos.dtos.execution;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.Instant;

public record ExecutionCreateRequest(
        @NotBlank String scheduleId,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dueAt
) {}
