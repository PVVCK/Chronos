package com.airtribe.chronos.controllers;

import com.airtribe.chronos.dtos.schedule.ScheduleCreateRequest;
import com.airtribe.chronos.dtos.schedule.ScheduleDTO;
import com.airtribe.chronos.dtos.schedule.ScheduleUpdateRequest;
import com.airtribe.chronos.exception.ExecutionFailed;
import com.airtribe.chronos.response.APIResponse;
import com.airtribe.chronos.services.service.ScheduleService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;

import jakarta.validation.Valid;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;


    @PostMapping("/create/{jobId}")
    public ResponseEntity<APIResponse<ScheduleDTO>> createSchedule(
            @PathVariable String jobId,
            @Valid @RequestBody ScheduleCreateRequest request, BindingResult result
    ) {
        if(result.hasErrors())
        {
            StringBuilder errorMessage = new StringBuilder("Validation errors Occured: ");
            result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(", "));

            throw new ExecutionFailed(errorMessage.toString());
        }

        ScheduleDTO schedule = scheduleService.createSchedule(jobId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body((APIResponse.success(schedule)));
    }


    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<ScheduleDTO>> getScheduleById(@PathVariable String id) {
        ScheduleDTO schedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Schedule not found: " + id));
        return ResponseEntity.ok(APIResponse.success(schedule));
    }


    @GetMapping("/job/{jobId}")
    public ResponseEntity<APIResponse<List<ScheduleDTO>>> getSchedulesByJobId(@PathVariable String jobId) {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByJobId(jobId);
        return ResponseEntity.ok(APIResponse.success(schedules));
    }


    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<ScheduleDTO>> updateSchedule(
            @PathVariable String id,
            @Valid @RequestBody ScheduleUpdateRequest request
    ) {
        ScheduleDTO schedule = scheduleService.updateSchedule(id, request);
        return ResponseEntity.ok(APIResponse.success(schedule));
    }


    @PatchMapping("/{id}/enable")
    public ResponseEntity<APIResponse<Void>> enableSchedule(@PathVariable String id) {
        scheduleService.enableSchedule(id);
        return ResponseEntity.ok(APIResponse.success(null));
    }


    @PatchMapping("/{id}/disable")
    public ResponseEntity<APIResponse<Void>> disableSchedule(@PathVariable String id) {
        scheduleService.disableSchedule(id);
        return ResponseEntity.ok(APIResponse.success(null));
    }


    @GetMapping("/due")
    public ResponseEntity<APIResponse<List<ScheduleDTO>>> getDueSchedules(
            @RequestParam Instant now
    ) {
        List<ScheduleDTO> schedules = scheduleService.getDueSchedules(now);
        return ResponseEntity.ok(APIResponse.success(schedules));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<ScheduleDTO>>> getAllSchedules() {
        List<ScheduleDTO> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(APIResponse.success(schedules));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteSchedule(@PathVariable String id) {
        scheduleService.softDeleteSchedule(id);
        return ResponseEntity.ok(APIResponse.success(null));
    }
}

