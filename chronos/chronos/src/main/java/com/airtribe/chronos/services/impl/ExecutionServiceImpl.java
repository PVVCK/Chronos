package com.airtribe.chronos.services.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airtribe.chronos.dtos.execution.ExecutionDTO;
import com.airtribe.chronos.entities.Execution;
import com.airtribe.chronos.entities.Schedule;
import com.airtribe.chronos.enums.ExecutionStatus;
import com.airtribe.chronos.repos.ExecutionRepository;
import com.airtribe.chronos.repos.ScheduleRepository;
import com.airtribe.chronos.services.service.ExecutionService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExecutionServiceImpl implements ExecutionService {

    private final ExecutionRepository executionRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    @CacheEvict(value = "Cache_Execution_All", allEntries = true)
    public ExecutionDTO createPending(String scheduleId, Instant dueAt) {
        Schedule schedule = scheduleRepository.findByIdAndDeletedFalse(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found: " + scheduleId));

        Execution execution = Execution.builder()
                .schedule(schedule)
                .dueAt(dueAt)
                .attemptCount(0)
                .status(ExecutionStatus.PENDING)
                .enqueuedAt(Instant.now())
                .build();

        return executionRepository.save(execution).toDTO();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"Cache_Execution", "Cache_Execution_All"}, key = "#executionId", allEntries = true)
    public void startExecution(String executionId, Instant startedAt) {
        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new EntityNotFoundException("Execution not found: " + executionId));

        executionRepository.markStarted(executionId, startedAt);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"Cache_Execution", "Cache_Execution_All"}, key = "#executionId", allEntries = true)
    public void finishExecution(String executionId, boolean success, Instant finishedAt) {
        ExecutionStatus status = success ? ExecutionStatus.SUCCEEDED : ExecutionStatus.FAILED;

        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new EntityNotFoundException("Execution not found: " + executionId));

        executionRepository.markFinished(executionId, status, finishedAt);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Cache_Execution", key = "#id")
    public ExecutionDTO getExecutionById(String id) {
        return executionRepository.findById(id)
                .map(Execution::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Execution not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Cache_Execution_All", key = "#cutoffTime")
    public List<ExecutionDTO> getDueExecutions(Instant cutoffTime) {
        return executionRepository.findByStatusAndDueAtBefore(ExecutionStatus.PENDING, cutoffTime)
                .stream()
                .map(Execution::toDTO)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"Cache_Execution", "Cache_Execution_All"}, allEntries = true)
    public void cancelExecution(String executionId, String reason, Instant cancelledAt) {
        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new EntityNotFoundException("Execution not found: " + executionId));

        if (execution.getStatus() == ExecutionStatus.SUCCEEDED ||
            execution.getStatus() == ExecutionStatus.FAILED ||
            execution.getStatus() == ExecutionStatus.CANCELLED) {
            throw new IllegalStateException("Execution cannot be cancelled in status: " + execution.getStatus());
        }

        execution.setStatus(ExecutionStatus.CANCELLED);
        execution.setFinishedAt(cancelledAt != null ? cancelledAt : Instant.now());
        execution.setCancelReason(reason);

        executionRepository.save(execution);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Cache_Execution_All")
    public List<ExecutionDTO> getAllExecutions() {
        return executionRepository.findAll()
                .stream()
                .map(Execution::toDTO)
                .toList();
    }

}
