package com.airtribe.chronos.services.service;


import com.airtribe.chronos.dtos.execution.AttemptDTO;

import java.time.Instant;
import java.util.List;

public interface AttemptService {

    AttemptDTO createAttempt(String executionId, String workerId, int attemptNo, Instant startedAt);

    AttemptDTO markSuccess(String attemptId, Instant finishedAt);

    AttemptDTO markFailed(String attemptId, String errorMessage, Instant finishedAt);

    List<AttemptDTO> getAttemptsByExecution(String executionId);

    AttemptDTO getAttemptById(String attemptId);
}
