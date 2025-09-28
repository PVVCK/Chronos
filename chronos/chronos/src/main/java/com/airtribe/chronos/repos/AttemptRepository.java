package com.airtribe.chronos.repos;

import com.airtribe.chronos.entities.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttemptRepository extends JpaRepository<Attempt, String> {
    List<Attempt> findByExecutionId(String executionId);
}
