package com.airtribe.chronos.services.service;

import com.airtribe.chronos.dtos.job.*;

import java.util.List;

public interface JobService {

    JobDTO createJob(JobCreateRequest request);

    JobDTO updateJob(String jobId, JobUpdateRequest request);

    JobDTO getJobById(String jobId);

    List<JobDTO> getJobsByTenant(String tenantId);

    void softDeleteJob(String jobId);
}