package com.airtribe.chronos.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.airtribe.chronos.dtos.job.JobCreateRequest;
import com.airtribe.chronos.dtos.job.JobDTO;
import com.airtribe.chronos.dtos.job.JobUpdateRequest;
import com.airtribe.chronos.entities.Job;
import com.airtribe.chronos.repos.JobRepository;

import jakarta.persistence.EntityNotFoundException;

class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createJob_shouldReturnJobDTO() {
        JobCreateRequest request = mock(JobCreateRequest.class);
        Job job = mock(Job.class);
        JobDTO jobDTO = mock(JobDTO.class);

        when(jobRepository.save(any(Job.class))).thenReturn(job);
        when(job.toDTO()).thenReturn(jobDTO);

        JobDTO result = jobService.createJob(request);

        assertEquals(jobDTO, result);
        verify(jobRepository).save(any(Job.class));
    }

    @Test
    void updateJob_shouldReturnJobDTO() {
        String jobId = "job1";
        JobUpdateRequest request = mock(JobUpdateRequest.class);
        Job job = mock(Job.class);
        JobDTO jobDTO = mock(JobDTO.class);

        when(jobRepository.findByIdAndDeletedFalse(jobId)).thenReturn(Optional.of(job));
        when(jobRepository.save(job)).thenReturn(job);
        when(job.toDTO()).thenReturn(jobDTO);

        JobDTO result = jobService.updateJob(jobId, request);

        assertEquals(jobDTO, result);
        verify(jobRepository).findByIdAndDeletedFalse(jobId);
        verify(jobRepository).save(job);
    }

    @Test
    void updateJob_shouldThrowEntityNotFoundException() {
        when(jobRepository.findByIdAndDeletedFalse(anyString())).thenReturn(Optional.empty());
        JobUpdateRequest request = mock(JobUpdateRequest.class);
        assertThrows(EntityNotFoundException.class, () ->
                jobService.updateJob("invalid", request));
    }

    @Test
    void getJobById_shouldReturnJobDTO() {
        String jobId = "job1";
        Job job = mock(Job.class);
        JobDTO jobDTO = mock(JobDTO.class);

        when(jobRepository.findByIdAndDeletedFalse(jobId)).thenReturn(Optional.of(job));
        when(job.toDTO()).thenReturn(jobDTO);

        JobDTO result = jobService.getJobById(jobId);

        assertEquals(jobDTO, result);
    }

    @Test
    void getJobById_shouldThrowEntityNotFoundException() {
        when(jobRepository.findByIdAndDeletedFalse(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                jobService.getJobById("invalid"));
    }

    @Test
    void getJobsByTenant_shouldReturnList() {
        String tenantId = "tenant1";
        Job job = mock(Job.class);
        JobDTO jobDTO = mock(JobDTO.class);

        when(jobRepository.findByTenantIdAndDeletedFalse(tenantId)).thenReturn(List.of(job));
        when(job.toDTO()).thenReturn(jobDTO);

        List<JobDTO> result = jobService.getJobsByTenant(tenantId);

        assertEquals(1, result.size());
        assertEquals(jobDTO, result.get(0));
    }

    @Test
    void softDeleteJob_shouldSetDeletedAndSave() {
        String jobId = "job1";
        Job job = mock(Job.class);

        when(jobRepository.findByIdAndDeletedFalse(jobId)).thenReturn(Optional.of(job));

        jobService.softDeleteJob(jobId);

        verify(job).setDeleted(true);
        verify(job).setUpdatedAt(any(Instant.class));
        verify(jobRepository).save(job);
    }

    @Test
    void softDeleteJob_shouldThrowEntityNotFoundException() {
        when(jobRepository.findByIdAndDeletedFalse(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                jobService.softDeleteJob("invalid"));
    }
}
