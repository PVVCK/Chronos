package com.airtribe.chronos.repos;

import com.airtribe.chronos.entities.Execution;
import com.airtribe.chronos.enums.ExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ExecutionRepository extends JpaRepository<Execution, String> {

    // Fetch executions that are due and still pending/enqueued
    List<Execution> findByStatusAndDueAtBefore(ExecutionStatus status, Instant cutoffTime);

    @Transactional
    @Modifying
    @Query("UPDATE Execution e SET e.status = 'RUNNING', e.startedAt = :startedAt WHERE e.id = :id")
    void markStarted(String id, Instant startedAt);

    @Transactional
    @Modifying
    @Query("UPDATE Execution e SET e.status = :status, e.finishedAt = :finishedAt WHERE e.id = :id")
    void markFinished(String id, ExecutionStatus status, Instant finishedAt);
}
