package com.airtribe.chronos.services.service;

import com.airtribe.chronos.dtos.schedule.ScheduleCreateRequest;
import com.airtribe.chronos.dtos.schedule.ScheduleDTO;
import com.airtribe.chronos.dtos.schedule.ScheduleUpdateRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    ScheduleDTO createSchedule(String jobId, ScheduleCreateRequest request);
    Optional<ScheduleDTO> getScheduleById(String id);
    List<ScheduleDTO> getSchedulesByJobId(String jobId);
    ScheduleDTO updateSchedule(String id, ScheduleUpdateRequest request);
    void enableSchedule(String id);
    void disableSchedule(String id);
    List<ScheduleDTO> getDueSchedules(Instant now);
    void softDeleteSchedule(String id);
    public List<ScheduleDTO> getAllSchedules();

}
