package com.airtribe.chronos.controllers;

import com.airtribe.chronos.dtos.execution.ExecutionCancelRequest;
import com.airtribe.chronos.dtos.execution.ExecutionCreateRequest;
import com.airtribe.chronos.dtos.execution.ExecutionDTO;
import com.airtribe.chronos.exception.ExecutionFailed;
import com.airtribe.chronos.response.APIResponse;
import com.airtribe.chronos.services.service.ExecutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/api/executions")
@RequiredArgsConstructor
public class ExecutionController {

    private final ExecutionService executionService;


    @PostMapping("/create")
    public ResponseEntity<APIResponse<ExecutionDTO>> createExecution(
            @Valid @RequestBody ExecutionCreateRequest request,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation errors occurred: ");
            result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(", "));
            throw new ExecutionFailed(errorMessage.toString()); // your centralized handler will catch this
        }

        ExecutionDTO execution = executionService.createPending(request.scheduleId(), request.dueAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(execution));
    }



    @PostMapping("/{id}/start")
    public ResponseEntity<APIResponse<Void>> startExecution(
            @PathVariable String id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startedAt
    ) {
        executionService.startExecution(id, startedAt != null ? startedAt : Instant.now());
        return ResponseEntity.ok(APIResponse.success(null));
    }


    @PostMapping("/{id}/finish")
    public ResponseEntity<APIResponse<Void>> finishExecution(
            @PathVariable String id,
            @RequestParam boolean success,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant finishedAt
    ) {
        executionService.finishExecution(id, success, finishedAt != null ? finishedAt : Instant.now());
        return ResponseEntity.ok(APIResponse.success(null));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<APIResponse<Void>> cancelExecution(
            @PathVariable String id,
            @Valid @RequestBody ExecutionCancelRequest request, BindingResult result
    ) {

        if(result.hasErrors())
        {
            StringBuilder errorMessage = new StringBuilder("Validation errors Occured: ");
            result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(", "));

            throw new ExecutionFailed(errorMessage.toString());
        }
        executionService.cancelExecution(id, request.reason(), Instant.now());
        return ResponseEntity.ok(APIResponse.success(null));
    }


    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<ExecutionDTO>> getExecutionById(@PathVariable String id) {
        ExecutionDTO execution = executionService.getExecutionById(id);
        return ResponseEntity.ok(APIResponse.success(execution));
    }


    @GetMapping("/due")
    public ResponseEntity<APIResponse<List<ExecutionDTO>>> getDueExecutions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant cutoffTime
    ) {
        List<ExecutionDTO> executions = executionService.getDueExecutions(cutoffTime);
        return ResponseEntity.ok(APIResponse.success(executions));
    }
    
 // Get all executions
    @GetMapping
    public ResponseEntity<APIResponse<List<ExecutionDTO>>> getAllExecutions() {
        List<ExecutionDTO> executions = executionService.getAllExecutions();
        return ResponseEntity.ok(APIResponse.success(executions));
    }

}
