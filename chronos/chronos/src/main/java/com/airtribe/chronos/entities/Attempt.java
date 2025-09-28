package com.airtribe.chronos.entities;

import com.airtribe.chronos.dtos.execution.AttemptDTO;
import com.airtribe.chronos.dtos.job.JobDTO;
import com.airtribe.chronos.enums.AttemptStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Instant;
@Entity
@Table(name = "attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "execution_id", nullable = false)
    private Execution execution;

    private int attemptNo;

    private String workerId;

    @Enumerated(EnumType.STRING)
    private AttemptStatus status;

    private Instant startedAt;

    private Instant finishedAt;

    private String errorMessage;

    private String logsUri;

    // ------------------- Methods -------------------
    public void markFailed(String error, Instant finishedAt) {
        this.status = AttemptStatus.FAILED;
        this.errorMessage = error;
        this.finishedAt = finishedAt;
    }

    public void markSuccess(Instant finishedAt) {
        this.status = AttemptStatus.SUCCEEDED;
        this.finishedAt = finishedAt;
    }

    public AttemptDTO toDTO() {
        return new AttemptDTO(
                this.id,
                this.execution != null ? this.execution.getId() : null,
                this.attemptNo,
                this.workerId,
                this.status,
                this.startedAt,
                this.finishedAt,
                this.errorMessage,
                this.logsUri
        );
    }

}
