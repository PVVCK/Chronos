package com.airtribe.chronos.valueobjects;


import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable runtime context passed to JobExecutors.
 * Contains execution metadata and deadline information.
 */

public final class ExecutionContext {

    private final String workerId;
    private final String correlationId;
    private final Instant deadline;
    private final Map<String, String> metadata;

    /**
     * Primary constructor.
     *
     * @param workerId      unique id of the worker executing this job
     * @param correlationId correlation id to track this execution across systems
     * @param deadline      time after which execution should be considered expired
     * @param metadata      additional key-value pairs (tags, env, runtime info)
     */
    public ExecutionContext(String workerId,
                            String correlationId,
                            Instant deadline,
                            Map<String, String> metadata) {

        this.workerId = Objects.requireNonNull(workerId, "workerId must not be null");
        this.correlationId = Objects.requireNonNull(correlationId, "correlationId must not be null");
        this.deadline = Objects.requireNonNull(deadline, "deadline must not be null");


        if (metadata == null) {
            this.metadata = Collections.emptyMap();
        } else {
            this.metadata = Collections.unmodifiableMap(new HashMap<>(metadata));
        }
    }

    /** Returns true if the execution deadline has passed. */
    public boolean isExpired() {
        return Instant.now().isAfter(deadline);
    }

    public String getWorkerId() { return workerId; }
    public String getCorrelationId() { return correlationId; }
    public Instant getDeadline() { return deadline; }

    /**
     * @return an unmodifiable map of metadata (safe to expose)
     */
    public Map<String, String> getMetadata() { return metadata; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecutionContext)) return false;
        ExecutionContext that = (ExecutionContext) o;
        return workerId.equals(that.workerId) &&
                correlationId.equals(that.correlationId) &&
                deadline.equals(that.deadline) &&
                metadata.equals(that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workerId, correlationId, deadline, metadata);
    }

    @Override
    public String toString() {
        return "ExecutionContext{" +
                "workerId='" + workerId + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", deadline=" + deadline +
                ", metadata=" + metadata +
                '}';
    }
}
