package com.airtribe.chronos.repos;

import com.airtribe.chronos.entities.Schedule;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {

    Optional<Schedule> findByIdAndDeletedFalse(String id);

    List<Schedule> findByJobIdAndDeletedFalse(String jobId);

    @Query("SELECT s FROM Schedule s WHERE s.deleted = false AND s.enabled = true AND s.nextRunAt <= :upto")
    List<Schedule> findDueSchedules(Instant upto);

    @Transactional
    @Modifying
    @Query("UPDATE Schedule s SET s.nextRunAt = :nextRunAt WHERE s.id = :scheduleId AND s.deleted = false")
    void updateNextRunAt(String scheduleId, Instant nextRunAt);

    @Transactional
    @Modifying
    @Query("UPDATE Schedule s SET s.deleted = true WHERE s.id = :id")
    void softDelete(String id);
}
