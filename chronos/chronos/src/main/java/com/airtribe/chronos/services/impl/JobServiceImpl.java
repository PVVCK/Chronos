package com.airtribe.chronos.services.impl;

import com.airtribe.chronos.dtos.job.*;
import com.airtribe.chronos.entities.Job;
import com.airtribe.chronos.repos.JobRepository;
import com.airtribe.chronos.services.service.JobService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    @Override
    @Transactional
    @CacheEvict(value = "Cache_Job_All", allEntries = true)
    public JobDTO createJob(JobCreateRequest request) {
        Job job = Job.builder()
                .tenantId(request.tenantId())
                .name(request.name())
                .type(request.type())
                .payload(request.payload())
                .timeout(request.timeout())
                .maxAttempts(request.maxAttempts())
                .retryPolicyId(request.retryPolicyId())
                .ownerUserId(request.ownerUserId())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .deleted(false)
                .build();

        return jobRepository.save(job).toDTO();
    }

    @Override
    @Transactional
    @CachePut(value = "Cache_Job", key = "#jobId")
    @CacheEvict(value = "Cache_Job_All", allEntries = true)
    public JobDTO updateJob(String jobId, JobUpdateRequest request) {
        Job job = jobRepository.findByIdAndDeletedFalse(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found: " + jobId));

        if (request.name() != null) job.setName(request.name());
        if (request.payload() != null) job.setPayload(request.payload());
        if (request.timeout() != null) job.setTimeout(request.timeout());
        if (request.maxAttempts() != null) job.setMaxAttempts(request.maxAttempts());
        if (request.retryPolicyId() != null) job.setRetryPolicyId(request.retryPolicyId());

        job.setUpdatedAt(Instant.now());

        return jobRepository.save(job).toDTO();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Cache_Job", key = "#jobId")
    public JobDTO getJobById(String jobId) {
        return jobRepository.findByIdAndDeletedFalse(jobId)
                .map(Job::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Job not found: " + jobId));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Cache_Job_All", key = "#tenantId")
    public List<JobDTO> getJobsByTenant(String tenantId) {
        return jobRepository.findByTenantIdAndDeletedFalse(tenantId)
                .stream()
                .map(Job::toDTO)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"Cache_Job", "Cache_Job_All"}, key = "#jobId", allEntries = true)
    public void softDeleteJob(String jobId) {
        Job job = jobRepository.findByIdAndDeletedFalse(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found: " + jobId));

        job.setDeleted(true);
        job.setUpdatedAt(Instant.now());
        jobRepository.save(job);
    }
}
