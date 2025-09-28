package com.airtribe.chronos.controllers;

import com.airtribe.chronos.dtos.retry.RetryPolicyCreateRequest;
import com.airtribe.chronos.dtos.retry.RetryPolicyDTO;
import com.airtribe.chronos.dtos.retry.RetryPolicyUpdateRequest;
import com.airtribe.chronos.exception.ExecutionFailed;
import com.airtribe.chronos.response.APIResponse;
import com.airtribe.chronos.services.service.RetryPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/api/retry-policies")
@RequiredArgsConstructor
public class RetryPolicyController {

    private final RetryPolicyService retryPolicyService;

    @PostMapping("/create")
    public ResponseEntity<APIResponse<RetryPolicyDTO>> createPolicy(
            @Valid @RequestBody RetryPolicyCreateRequest request,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation errors occurred: ");
            result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(", "));
            throw new ExecutionFailed(errorMessage.toString());
        }

        RetryPolicyDTO policy = retryPolicyService.createPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(policy));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<RetryPolicyDTO>> updatePolicy(
            @PathVariable String id,
            @Valid @RequestBody RetryPolicyUpdateRequest request,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation errors occurred: ");
            result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(", "));
            throw new ExecutionFailed(errorMessage.toString());
        }

        RetryPolicyDTO updatedPolicy = retryPolicyService.updatePolicy(id, request);
        return ResponseEntity.ok(APIResponse.success(updatedPolicy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<RetryPolicyDTO>> getPolicyById(@PathVariable String id) {
        RetryPolicyDTO policy = retryPolicyService.getPolicyById(id);
        return ResponseEntity.ok(APIResponse.success(policy));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<RetryPolicyDTO>>> getAllPolicies() {
        List<RetryPolicyDTO> policies = retryPolicyService.getAllPolicies();
        return ResponseEntity.ok(APIResponse.success(policies));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deletePolicy(@PathVariable String id) {
        retryPolicyService.deletePolicy(id);
        return ResponseEntity.ok(APIResponse.success(null));
    }
}

