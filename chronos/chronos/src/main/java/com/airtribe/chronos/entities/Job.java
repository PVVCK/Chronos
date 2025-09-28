package com.airtribe.chronos.entities;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Duration;
import java.time.Instant;

import com.airtribe.chronos.enums.JobType;
import com.airtribe.chronos.dtos.job.JobDTO;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String tenantId;

    private String name;

    @Enumerated(EnumType.STRING)
    private JobType type;

    @Lob
    private String payload;

    private Duration timeout;

    private int maxAttempts;

    private String retryPolicyId;

    private String ownerUserId;

    private Instant createdAt;

    private Instant updatedAt;

    @Column(nullable = false)
    private  @Builder.Default  boolean deleted = false;

    // ------------------- Methods -------------------
    public JobDTO toDTO() {
        return new JobDTO(id, tenantId, name, type, payload, timeout, maxAttempts, retryPolicyId, ownerUserId, createdAt, updatedAt);
    }

    public static Job fromDTO(JobDTO dto) {
        return Job.builder()
                .id(dto.id())
                .tenantId(dto.tenantId())
                .name(dto.name())
                .type(dto.type())
                .payload(dto.payload())
                .timeout(dto.timeout())
                .maxAttempts(dto.maxAttempts())
                .retryPolicyId(dto.retryPolicyId())
                .ownerUserId(dto.ownerUserId())
                .createdAt(dto.createdAt())
                .updatedAt(dto.updatedAt())
                .deleted(false)
                .build();
    }
}

