package com.airtribe.chronos.dtos.schedule;

import com.airtribe.chronos.enums.ScheduleType;
import com.airtribe.chronos.enums.MisfirePolicy;
import jakarta.validation.constraints.*;

import java.time.Instant;

public record ScheduleCreateRequest(
        @NotNull ScheduleType scheduleType,
        @NotBlank String cronExpr,
        @NotNull @Min(1) Long intervalSec,
        @NotNull String startAt,
        String endAt,
        @NotBlank String timezone,
        @NotNull MisfirePolicy misfirePolicy,
        boolean enabled
) {}
