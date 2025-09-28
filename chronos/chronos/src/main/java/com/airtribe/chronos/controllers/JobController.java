package com.airtribe.chronos.controllers;

import com.airtribe.chronos.dtos.job.JobCreateRequest;
import com.airtribe.chronos.dtos.job.JobDTO;
import com.airtribe.chronos.dtos.job.JobUpdateRequest;
import com.airtribe.chronos.exception.ExecutionFailed;
import com.airtribe.chronos.response.APIResponse;
import com.airtribe.chronos.services.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;


    @PostMapping("/create")
    public ResponseEntity<APIResponse<JobDTO>> createJob(@Valid @RequestBody JobCreateRequest request, BindingResult result) {

        if(result.hasErrors())
        {
            StringBuilder errorMessage = new StringBuilder("Validation errors Occured: ");
            result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(", "));

            throw new ExecutionFailed(errorMessage.toString());
        }
        JobDTO job = jobService.createJob(request);
        return ResponseEntity.status(HttpStatus.CREATED).body((APIResponse.success(job)));
    }


    @PutMapping("/{jobId}")
    public ResponseEntity<APIResponse<JobDTO>> updateJob(
            @PathVariable String jobId,
            @Valid @RequestBody JobUpdateRequest request
    ) {
        JobDTO job = jobService.updateJob(jobId, request);
        return ResponseEntity.ok(APIResponse.success(job));
    }


    @GetMapping("/{jobId}")
    public ResponseEntity<APIResponse<JobDTO>> getJobById(@PathVariable String jobId) {
        JobDTO job = jobService.getJobById(jobId);
        return ResponseEntity.ok(APIResponse.success(job));
    }


    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<APIResponse<List<JobDTO>>> getJobsByTenant(@PathVariable String tenantId) {
        List<JobDTO> jobs = jobService.getJobsByTenant(tenantId);
        return ResponseEntity.ok(APIResponse.success(jobs));
    }


    @DeleteMapping("/{jobId}")
    public ResponseEntity<APIResponse<Void>> softDeleteJob(@PathVariable String jobId) {
        jobService.softDeleteJob(jobId);
        return ResponseEntity.ok(APIResponse.success(null));
    }

}

