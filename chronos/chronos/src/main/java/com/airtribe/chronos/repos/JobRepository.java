package com.airtribe.chronos.repos;

import com.airtribe.chronos.entities.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, String> {

    // Only return non-deleted jobs
    Optional<Job> findByIdAndDeletedFalse(String id);

    List<Job> findByTenantIdAndDeletedFalse(String tenantId);

    @Transactional
    @Modifying
    @Query("UPDATE Job j SET j.deleted = true, j.updatedAt = CURRENT_TIMESTAMP WHERE j.id = :id")
    void softDelete(String id);
}
