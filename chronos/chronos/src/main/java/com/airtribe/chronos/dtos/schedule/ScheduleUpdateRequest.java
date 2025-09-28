package com.airtribe.chronos.dtos.schedule;

import com.airtribe.chronos.enums.ScheduleType;
import com.airtribe.chronos.enums.MisfirePolicy;

import java.time.Instant;

public record ScheduleUpdateRequest(
        ScheduleType scheduleType,
        String cronExpr,
        Long intervalSec,
        String startAt,
        String endAt,
        String timezone,
        MisfirePolicy misfirePolicy,
        Boolean enabled
) {}
