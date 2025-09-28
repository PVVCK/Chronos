package com.airtribe.chronos.entities;


import com.airtribe.chronos.dtos.execution.ExecutionDTO;
import com.airtribe.chronos.enums.ExecutionStatus;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "executions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Execution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    private ExecutionStatus status;

    private Instant dueAt;

    private Instant enqueuedAt;

    private Instant startedAt;

    private Instant finishedAt;

    private int attemptCount;

    private String correlationId;

    @Column(name = "cancel_reason")
    private String cancelReason;


    // ------------------- Methods -------------------
    public void markEnqueued(Instant enqueuedAt) {
        this.enqueuedAt = enqueuedAt;
        this.status = ExecutionStatus.PENDING;
    }

    public void markStarted(Instant startedAt) {
        this.startedAt = startedAt;
        this.status = ExecutionStatus.RUNNING;
    }

    public void markFinished(ExecutionStatus status, Instant finishedAt) {
        this.finishedAt = finishedAt;
        this.status = status;
    }

    public ExecutionDTO toDTO() {
        String scheduleId = (this.schedule != null) ? this.schedule.getId() : null;
        String jobId = null;
        // schedule.getJob() is LAZY — this works inside @Transactional
        if (this.schedule != null && this.schedule.getJob() != null) {
            jobId = this.schedule.getJob().getId();
        }

        return new ExecutionDTO(
                this.id,
                scheduleId,
                jobId,
                this.status,
                this.dueAt,
                this.enqueuedAt,
                this.startedAt,
                this.finishedAt,
                this.attemptCount,
                this.correlationId
        );
    }
}
