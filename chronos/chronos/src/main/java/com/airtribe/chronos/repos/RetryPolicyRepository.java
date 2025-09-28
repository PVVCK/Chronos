package com.airtribe.chronos.repos;

import com.airtribe.chronos.entities.RetryPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RetryPolicyRepository extends JpaRepository<RetryPolicy, String> {
//    Optional<RetryPolicy> findById(String id);
}
