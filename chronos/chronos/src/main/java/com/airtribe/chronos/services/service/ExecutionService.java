package com.airtribe.chronos.services.service;

import com.airtribe.chronos.dtos.execution.ExecutionCancelRequest;
import com.airtribe.chronos.dtos.execution.ExecutionDTO;

import java.time.Instant;
import java.util.List;


public interface ExecutionService {
    ExecutionDTO createPending(String scheduleId, Instant dueAt);

    void startExecution(String executionId, Instant startedAt);

    void finishExecution(String executionId, boolean success, Instant finishedAt);

    ExecutionDTO getExecutionById(String id);

    List<ExecutionDTO> getDueExecutions(Instant cutoffTime);

    void cancelExecution(String executionId, String reason, Instant cancelledAt);
    
    List<ExecutionDTO> getAllExecutions();

}