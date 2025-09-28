package com.airtribe.chronos.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.airtribe.chronos.dtos.execution.ExecutionDTO;
import com.airtribe.chronos.entities.Execution;
import com.airtribe.chronos.entities.Schedule;
import com.airtribe.chronos.enums.ExecutionStatus;
import com.airtribe.chronos.repos.ExecutionRepository;
import com.airtribe.chronos.repos.ScheduleRepository;

import jakarta.persistence.EntityNotFoundException;

class ExecutionServiceImplTest {

    @Mock
    private ExecutionRepository executionRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ExecutionServiceImpl executionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPending_shouldReturnExecutionDTO() {
        String scheduleId = "sch1";
        Instant dueAt = Instant.now();
        Schedule schedule = mock(Schedule.class);
        Execution execution = mock(Execution.class);
        ExecutionDTO executionDTO = mock(ExecutionDTO.class);

        when(scheduleRepository.findByIdAndDeletedFalse(scheduleId)).thenReturn(Optional.of(schedule));
        when(executionRepository.save(any(Execution.class))).thenReturn(execution);
        when(execution.toDTO()).thenReturn(executionDTO);

        ExecutionDTO result = executionService.createPending(scheduleId, dueAt);

        assertEquals(executionDTO, result);
        verify(scheduleRepository).findByIdAndDeletedFalse(scheduleId);
        verify(executionRepository).save(any(Execution.class));
    }

    @Test
    void createPending_shouldThrowEntityNotFoundException() {
        when(scheduleRepository.findByIdAndDeletedFalse(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                executionService.createPending("invalid", Instant.now()));
    }

    @Test
    void startExecution_shouldCallMarkStarted() {
        String executionId = "exec1";
        Instant startedAt = Instant.now();
        Execution execution = mock(Execution.class);

        when(executionRepository.findById(executionId)).thenReturn(Optional.of(execution));

        executionService.startExecution(executionId, startedAt);

        verify(executionRepository).markStarted(executionId, startedAt);
    }

    @Test
    void startExecution_shouldThrowEntityNotFoundException() {
        when(executionRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                executionService.startExecution("invalid", Instant.now()));
    }

    @Test
    void finishExecution_shouldCallMarkFinished() {
        String executionId = "exec1";
        Instant finishedAt = Instant.now();
        Execution execution = mock(Execution.class);

        when(executionRepository.findById(executionId)).thenReturn(Optional.of(execution));

        executionService.finishExecution(executionId, true, finishedAt);

        verify(executionRepository).markFinished(executionId, ExecutionStatus.SUCCEEDED, finishedAt);
    }

    @Test
    void finishExecution_shouldThrowEntityNotFoundException() {
        when(executionRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                executionService.finishExecution("invalid", true, Instant.now()));
    }

    @Test
    void getExecutionById_shouldReturnExecutionDTO() {
        String id = "exec1";
        Execution execution = mock(Execution.class);
        ExecutionDTO executionDTO = mock(ExecutionDTO.class);

        when(executionRepository.findById(id)).thenReturn(Optional.of(execution));
        when(execution.toDTO()).thenReturn(executionDTO);

        ExecutionDTO result = executionService.getExecutionById(id);

        assertEquals(executionDTO, result);
    }

    @Test
    void getExecutionById_shouldThrowEntityNotFoundException() {
        when(executionRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                executionService.getExecutionById("invalid"));
    }

    @Test
    void getDueExecutions_shouldReturnList() {
        Instant cutoffTime = Instant.now();
        Execution execution = mock(Execution.class);
        ExecutionDTO executionDTO = mock(ExecutionDTO.class);

        when(executionRepository.findByStatusAndDueAtBefore(ExecutionStatus.PENDING, cutoffTime))
                .thenReturn(List.of(execution));
        when(execution.toDTO()).thenReturn(executionDTO);

        List<ExecutionDTO> result = executionService.getDueExecutions(cutoffTime);

        assertEquals(1, result.size());
        assertEquals(executionDTO, result.get(0));
    }

    @Test
    void cancelExecution_shouldUpdateStatusAndSave() {
        String executionId = "exec1";
        String reason = "cancelled";
        Instant cancelledAt = Instant.now();
        Execution execution = mock(Execution.class);

        when(executionRepository.findById(executionId)).thenReturn(Optional.of(execution));
        when(execution.getStatus()).thenReturn(ExecutionStatus.PENDING);

        executionService.cancelExecution(executionId, reason, cancelledAt);

        verify(execution).setStatus(ExecutionStatus.CANCELLED);
        verify(execution).setFinishedAt(cancelledAt);
        verify(execution).setCancelReason(reason);
        verify(executionRepository).save(execution);
    }

    @Test
    void cancelExecution_shouldThrowEntityNotFoundException() {
        when(executionRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                executionService.cancelExecution("invalid", "reason", Instant.now()));
    }

    @Test
    void cancelExecution_shouldThrowIllegalStateException() {
        String executionId = "exec1";
        Execution execution = mock(Execution.class);

        when(executionRepository.findById(executionId)).thenReturn(Optional.of(execution));
        when(execution.getStatus()).thenReturn(ExecutionStatus.SUCCEEDED);

        assertThrows(IllegalStateException.class, () ->
                executionService.cancelExecution(executionId, "reason", Instant.now()));
    }
}
