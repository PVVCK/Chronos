package com.airtribe.chronos.entities;



import com.airtribe.chronos.dtos.schedule.ScheduleDTO;
import com.airtribe.chronos.enums.MisfirePolicy;
import com.airtribe.chronos.enums.ScheduleType;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Duration;
import java.time.Instant;
@Entity
@Table(name = "schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    private String cronExpr;

    private long intervalSec;

    private Instant startAt;

    private Instant endAt;

    private String timezone;

    @Enumerated(EnumType.STRING)
    private MisfirePolicy misfirePolicy;

    private boolean enabled;

    private Instant nextRunAt;

    private Instant lastRunAt;

    @Column(nullable = false)
    private   @Builder.Default  boolean deleted = false;

    // ------------------- Methods -------------------
    public boolean isDue(Instant now) {
        return enabled && nextRunAt != null && !now.isBefore(nextRunAt);
    }

    public ScheduleDTO toDTO() {
        return new ScheduleDTO(
                id,
                job.getId(),
                scheduleType,
                cronExpr,
                intervalSec,
                startAt,
                endAt,
                timezone,
                misfirePolicy,
                enabled,
                nextRunAt,
                lastRunAt
        );
    }

}

