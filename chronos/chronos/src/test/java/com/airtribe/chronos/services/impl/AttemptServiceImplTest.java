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

import com.airtribe.chronos.dtos.execution.AttemptDTO;
import com.airtribe.chronos.entities.Attempt;
import com.airtribe.chronos.entities.Execution;
import com.airtribe.chronos.repos.AttemptRepository;
import com.airtribe.chronos.repos.ExecutionRepository;

import jakarta.persistence.EntityNotFoundException;

class AttemptServiceImplTest {

    @Mock
    private AttemptRepository attemptRepository;

    @Mock
    private ExecutionRepository executionRepository;

    @InjectMocks
    private AttemptServiceImpl attemptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAttempt_shouldReturnAttemptDTO() {
        String executionId = "exec1";
        String workerId = "worker1";
        int attemptNo = 1;
        Instant startedAt = Instant.now();

        Execution execution = mock(Execution.class);
        Attempt attempt = mock(Attempt.class);
        AttemptDTO attemptDTO = mock(AttemptDTO.class);

        when(executionRepository.findById(executionId)).thenReturn(Optional.of(execution));
        when(attemptRepository.save(any(Attempt.class))).thenReturn(attempt);
        when(attempt.toDTO()).thenReturn(attemptDTO);

        AttemptDTO result = attemptService.createAttempt(executionId, workerId, attemptNo, startedAt);

        assertEquals(attemptDTO, result);
        verify(executionRepository).findById(executionId);
        verify(attemptRepository).save(any(Attempt.class));
    }

    @Test
    void createAttempt_shouldThrowEntityNotFoundException() {
        when(executionRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                attemptService.createAttempt("invalid", "worker", 1, Instant.now()));
    }

    @Test
    void markSuccess_shouldReturnAttemptDTO() {
        String attemptId = "attempt1";
        Attempt attempt = mock(Attempt.class);
        AttemptDTO attemptDTO = mock(AttemptDTO.class);

        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(attemptRepository.save(attempt)).thenReturn(attempt);
        when(attempt.toDTO()).thenReturn(attemptDTO);

        AttemptDTO result = attemptService.markSuccess(attemptId, Instant.now());

        assertEquals(attemptDTO, result);
        verify(attemptRepository).findById(attemptId);
        verify(attemptRepository).save(attempt);
    }

    @Test
    void markSuccess_shouldThrowEntityNotFoundException() {
        when(attemptRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                attemptService.markSuccess("invalid", Instant.now()));
    }

    @Test
    void markFailed_shouldReturnAttemptDTO() {
        String attemptId = "attempt1";
        Attempt attempt = mock(Attempt.class);
        AttemptDTO attemptDTO = mock(AttemptDTO.class);

        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(attemptRepository.save(attempt)).thenReturn(attempt);
        when(attempt.toDTO()).thenReturn(attemptDTO);

        // markFailed is void, so no need to doNothing here, Mockito allows it
        AttemptDTO result = attemptService.markFailed(attemptId, "error", Instant.now());

        assertEquals(attemptDTO, result);
    }

    @Test
    void markFailed_shouldThrowEntityNotFoundException() {
        when(attemptRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                attemptService.markFailed("invalid", "error", Instant.now()));
    }

    @Test
    void getAttemptsByExecution_shouldReturnList() {
        String executionId = "exec1";
        Attempt attempt = mock(Attempt.class);
        AttemptDTO attemptDTO = mock(AttemptDTO.class);

        when(attemptRepository.findByExecutionId(executionId)).thenReturn(List.of(attempt));
        when(attempt.toDTO()).thenReturn(attemptDTO);

        List<AttemptDTO> result = attemptService.getAttemptsByExecution(executionId);

        assertEquals(1, result.size());
        assertEquals(attemptDTO, result.get(0));
    }

    @Test
    void getAttemptById_shouldReturnAttemptDTO() {
        String attemptId = "attempt1";
        Attempt attempt = mock(Attempt.class);
        AttemptDTO attemptDTO = mock(AttemptDTO.class);

        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(attempt.toDTO()).thenReturn(attemptDTO);

        AttemptDTO result = attemptService.getAttemptById(attemptId);

        assertEquals(attemptDTO, result);
    }

    @Test
    void getAttemptById_shouldThrowEntityNotFoundException() {
        when(attemptRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                attemptService.getAttemptById("invalid"));
    }
}
