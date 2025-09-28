package com.airtribe.chronos.services.impl;

import com.airtribe.chronos.dtos.execution.AttemptDTO;
import com.airtribe.chronos.entities.Attempt;
import com.airtribe.chronos.entities.Execution;
import com.airtribe.chronos.enums.AttemptStatus;
import com.airtribe.chronos.repos.AttemptRepository;
import com.airtribe.chronos.repos.ExecutionRepository;
import com.airtribe.chronos.services.service.AttemptService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttemptServiceImpl implements AttemptService {

    private final AttemptRepository attemptRepository;
    private final ExecutionRepository executionRepository;

    @Override
    @Transactional
    @CacheEvict(value = "Cache_Attempt_ByExecution", key = "#executionId", allEntries = true)
    public AttemptDTO createAttempt(String executionId, String workerId, int attemptNo, Instant startedAt) {
        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new EntityNotFoundException("Execution not found: " + executionId));

        Attempt attempt = Attempt.builder()
                .execution(execution)
                .attemptNo(attemptNo)
                .workerId(workerId)
                .status(AttemptStatus.STARTED)
                .startedAt(startedAt != null ? startedAt : Instant.now())
                .build();

        return attemptRepository.save(attempt).toDTO();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"Cache_Attempt", "Cache_Attempt_ByExecution"}, key = "#attemptId", allEntries = true)
    public AttemptDTO markSuccess(String attemptId, Instant finishedAt) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new EntityNotFoundException("Attempt not found: " + attemptId));

        attempt.setStatus(AttemptStatus.SUCCEEDED);
        attempt.setFinishedAt(finishedAt != null ? finishedAt : Instant.now());

        return attemptRepository.save(attempt).toDTO();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"Cache_Attempt", "Cache_Attempt_ByExecution"}, key = "#attemptId", allEntries = true)
    public AttemptDTO markFailed(String attemptId, String errorMessage, Instant finishedAt) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new EntityNotFoundException("Attempt not found: " + attemptId));

        attempt.markFailed(errorMessage, finishedAt != null ? finishedAt : Instant.now());
        return attemptRepository.save(attempt).toDTO();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Cache_Attempt_ByExecution", key = "#executionId")
    public List<AttemptDTO> getAttemptsByExecution(String executionId) {
        return attemptRepository.findByExecutionId(executionId)
                .stream()
                .map(Attempt::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Cache_Attempt", key = "#attemptId")
    public AttemptDTO getAttemptById(String attemptId) {
        return attemptRepository.findById(attemptId)
                .map(Attempt::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Attempt not found: " + attemptId));
    }
}
