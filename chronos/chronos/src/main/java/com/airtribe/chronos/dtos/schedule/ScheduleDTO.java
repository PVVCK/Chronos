package com.airtribe.chronos.dtos.schedule;

import com.airtribe.chronos.enums.ScheduleType;
import com.airtribe.chronos.enums.MisfirePolicy;

import java.time.Instant;

public record ScheduleDTO(
        String id,
        String jobId,
        ScheduleType scheduleType,
        String cronExpr,
        Long intervalSec,
        Instant startAt,
        Instant endAt,
        String timezone,
        MisfirePolicy misfirePolicy,
        boolean enabled,
        Instant nextRunAt,
        Instant lastRunAt
) {}
