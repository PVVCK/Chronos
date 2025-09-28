package com.airtribe.chronos.interfaces;

import com.airtribe.chronos.entities.Schedule;

import java.time.Instant;
import java.util.List;

public interface TriggerCalculator {

    Instant next(Instant from, Schedule schedule);
    List<Instant> nextN(Instant from, Schedule schedule, int count);
}
