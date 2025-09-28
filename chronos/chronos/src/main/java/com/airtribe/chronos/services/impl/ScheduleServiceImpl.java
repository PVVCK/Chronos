package com.airtribe.chronos.services.impl;

import com.airtribe.chronos.dtos.schedule.ScheduleCreateRequest;
import com.airtribe.chronos.dtos.schedule.ScheduleDTO;
import com.airtribe.chronos.dtos.schedule.ScheduleUpdateRequest;
import com.airtribe.chronos.entities.Job;
import com.airtribe.chronos.entities.Schedule;
import com.airtribe.chronos.repos.JobRepository;
import com.airtribe.chronos.repos.ScheduleRepository;
import com.airtribe.chronos.services.service.ScheduleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional
    @CacheEvict(value = "Cache_Schedule_All", allEntries = true)
    public ScheduleDTO createSchedule(String jobId, ScheduleCreateRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found with id: " + jobId));
        Schedule schedule = Schedule.builder()
                .job(job)
                .scheduleType(request.scheduleType())
                .cronExpr(request.cronExpr())
                .intervalSec(request.intervalSec() != null ? request.intervalSec() : 0L)
                .startAt(parseDateTime(request.startAt()))
                .endAt(parseDateTime(request.endAt()))
                .timezone(request.timezone())
                .misfirePolicy(request.misfirePolicy())
                .enabled(request.enabled())
                .nextRunAt(parseDateTime(request.startAt()))
                .build();


        return scheduleRepository.save(schedule).toDTO();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Cache_Schedule", key = "#id")
    public Optional<ScheduleDTO> getScheduleById(String id) {
        return scheduleRepository.findByIdAndDeletedFalse(id).map(Schedule::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Cache_Schedule_All", key = "#jobId")
    public List<ScheduleDTO> getSchedulesByJobId(String jobId) {
        return scheduleRepository.findByJobIdAndDeletedFalse(jobId)
                .stream()
                .map(Schedule::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CachePut(value = "Cache_Schedule", key = "#id")
    @CacheEvict(value = "Cache_Schedule_All", allEntries = true)
    public ScheduleDTO updateSchedule(String id, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id: " + id));

        if (request.scheduleType() != null) schedule.setScheduleType(request.scheduleType());
        if (request.cronExpr() != null) schedule.setCronExpr(request.cronExpr());
        if (request.intervalSec() != null) schedule.setIntervalSec(request.intervalSec());
        if (request.startAt() != null) schedule.setStartAt(parseDateTime(request.startAt()));
        if (request.endAt() != null) schedule.setEndAt(parseDateTime(request.endAt()));
        if (request.timezone() != null) schedule.setTimezone(request.timezone());
        if (request.misfirePolicy() != null) schedule.setMisfirePolicy(request.misfirePolicy());
        if (request.enabled() != null) schedule.setEnabled(request.enabled());

        schedule.setNextRunAt(schedule.getStartAt()); // reset next run when updated

        return scheduleRepository.save(schedule).toDTO();
    }

    @Override
    @Transactional
    @CachePut(value = "Cache_Schedule", key = "#id")
    @CacheEvict(value = "Cache_Schedule_All", allEntries = true)
    public void enableSchedule(String id) {
        Schedule schedule = scheduleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id: " + id));
        schedule.setEnabled(true);
        scheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    @CachePut(value = "Cache_Schedule", key = "#id")
    @CacheEvict(value = "Cache_Schedule_All", allEntries = true)
    public void disableSchedule(String id) {
        Schedule schedule = scheduleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id: " + id));
        schedule.setEnabled(false);
        scheduleRepository.save(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Cache_Schedule_All", key = "#now")
    public List<ScheduleDTO> getDueSchedules(Instant now) {
        return scheduleRepository.findDueSchedules(now)
                .stream()
                .map(Schedule::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"Cache_Schedule", "Cache_Schedule_All"}, key = "#id", allEntries = true)
    public void softDeleteSchedule(String id) {
        Schedule schedule = scheduleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found: " + id));

        schedule.setDeleted(true);
        scheduleRepository.save(schedule);
    }
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(Schedule::toDTO)
                .toList();
    }


    // Helper method
    private Instant parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) return null;
        return Instant.parse(dateTimeStr); // accepts "2025-09-28T09:28:00.000Z"
    }

}
