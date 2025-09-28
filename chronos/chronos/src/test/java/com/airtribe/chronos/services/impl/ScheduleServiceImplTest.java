package com.airtribe.chronos.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.airtribe.chronos.dtos.schedule.ScheduleCreateRequest;
import com.airtribe.chronos.dtos.schedule.ScheduleDTO;
import com.airtribe.chronos.dtos.schedule.ScheduleUpdateRequest;
import com.airtribe.chronos.entities.Job;
import com.airtribe.chronos.entities.Schedule;
import com.airtribe.chronos.repos.JobRepository;
import com.airtribe.chronos.repos.ScheduleRepository;

import jakarta.persistence.EntityNotFoundException;

class ScheduleServiceImplTest {

    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSchedule_shouldReturnDTO() {
        String jobId = "job1";
        ScheduleCreateRequest request = mock(ScheduleCreateRequest.class);
        Job job = mock(Job.class);
        Schedule schedule = mock(Schedule.class);
        ScheduleDTO dto = mock(ScheduleDTO.class);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);
        when(schedule.toDTO()).thenReturn(dto);

        ScheduleDTO result = scheduleService.createSchedule(jobId, request);

        assertEquals(dto, result);
        verify(jobRepository).findById(jobId);
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    void createSchedule_shouldThrowEntityNotFoundException() {
        when(jobRepository.findById(anyString())).thenReturn(Optional.empty());
        ScheduleCreateRequest request = mock(ScheduleCreateRequest.class);
        assertThrows(EntityNotFoundException.class, () ->
                scheduleService.createSchedule("invalid", request));
    }

    @Test
    void getScheduleById_shouldReturnOptionalDTO() {
        String id = "sch1";
        Schedule schedule = mock(Schedule.class);
        ScheduleDTO dto = mock(ScheduleDTO.class);

        when(scheduleRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(schedule));
        when(schedule.toDTO()).thenReturn(dto);

        Optional<ScheduleDTO> result = scheduleService.getScheduleById(id);

        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
    }

    @Test
    void getScheduleById_shouldReturnEmptyOptional() {
        when(scheduleRepository.findByIdAndDeletedFalse(anyString())).thenReturn(Optional.empty());
        Optional<ScheduleDTO> result = scheduleService.getScheduleById("invalid");
        assertTrue(result.isEmpty());
    }

    @Test
    void getSchedulesByJobId_shouldReturnList() {
        String jobId = "job1";
        Schedule schedule = mock(Schedule.class);
        ScheduleDTO dto = mock(ScheduleDTO.class);

        when(scheduleRepository.findByJobIdAndDeletedFalse(jobId)).thenReturn(List.of(schedule));
        when(schedule.toDTO()).thenReturn(dto);

        List<ScheduleDTO> result = scheduleService.getSchedulesByJobId(jobId);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void updateSchedule_shouldReturnDTO() {
        String id = "sch1";
        ScheduleUpdateRequest request = mock(ScheduleUpdateRequest.class);
        Schedule schedule = mock(Schedule.class);
        ScheduleDTO dto = mock(ScheduleDTO.class);

        when(scheduleRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        when(schedule.toDTO()).thenReturn(dto);

        ScheduleDTO result = scheduleService.updateSchedule(id, request);

        assertEquals(dto, result);
        verify(scheduleRepository).findByIdAndDeletedFalse(id);
        verify(scheduleRepository).save(schedule);
    }

    @Test
    void updateSchedule_shouldThrowEntityNotFoundException() {
        when(scheduleRepository.findByIdAndDeletedFalse(anyString())).thenReturn(Optional.empty());
        ScheduleUpdateRequest request = mock(ScheduleUpdateRequest.class);
        assertThrows(EntityNotFoundException.class, () ->
                scheduleService.updateSchedule("invalid", request));
    }

    @Test
    void enableSchedule_shouldSetEnabledTrue() {
        String id = "sch1";
        Schedule schedule = mock(Schedule.class);

        when(scheduleRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(schedule));

        scheduleService.enableSchedule(id);

        verify(schedule).setEnabled(true);
        verify(scheduleRepository).save(schedule);
    }

    @Test
    void enableSchedule_shouldThrowEntityNotFoundException() {
        when(scheduleRepository.findByIdAndDeletedFalse(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                scheduleService.enableSchedule("invalid"));
    }

    @Test
    void disableSchedule_shouldSetEnabledFalse() {
        String id = "sch1";
        Schedule schedule = mock(Schedule.class);

        when(scheduleRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(schedule));

        scheduleService.disableSchedule(id);

        verify(schedule).setEnabled(false);
        verify(scheduleRepository).save(schedule);
    }

    @Test
    void disableSchedule_shouldThrowEntityNotFoundException() {
        when(scheduleRepository.findByIdAndDeletedFalse(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                scheduleService.disableSchedule("invalid"));
    }

    @Test
    void getDueSchedules_shouldReturnList() {
        Instant now = Instant.now();
        Schedule schedule = mock(Schedule.class);
        ScheduleDTO dto = mock(ScheduleDTO.class);

        when(scheduleRepository.findDueSchedules(now)).thenReturn(List.of(schedule));
        when(schedule.toDTO()).thenReturn(dto);

        List<ScheduleDTO> result = scheduleService.getDueSchedules(now);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void softDeleteSchedule_shouldSetDeletedAndSave() {
        String id = "sch1";
        Schedule schedule = mock(Schedule.class);

        when(scheduleRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(schedule));

        scheduleService.softDeleteSchedule(id);

        verify(schedule).setDeleted(true);
        verify(scheduleRepository).save(schedule);
    }

    @Test
    void softDeleteSchedule_shouldThrowEntityNotFoundException() {
        when(scheduleRepository.findByIdAndDeletedFalse(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                scheduleService.softDeleteSchedule("invalid"));
    }
}
