package com.airtribe.chronos.dtos.worker;

public record WorkerRegistrationRequest(
        String hostname,
        String ipAddress,
        String capabilities // optional JSON or comma-separated string
) {}