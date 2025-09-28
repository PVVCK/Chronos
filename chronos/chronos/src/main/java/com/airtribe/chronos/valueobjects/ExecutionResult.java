package com.airtribe.chronos.valueobjects;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable result of executing a Job.
 * Returned by JobExecutor implementations.
 */
public final class ExecutionResult {

    private final boolean success;
    private final String output;        // Optional: stdout/log snippet
    private final String errorMessage;  // Optional: failure reason
    private final int exitCode;         // Process exit or custom code
    private final Instant finishedAt;

    private ExecutionResult(boolean success,
                            String output,
                            String errorMessage,
                            int exitCode,
                            Instant finishedAt) {
        this.success = success;
        this.output = output;
        this.errorMessage = errorMessage;
        this.exitCode = exitCode;
        this.finishedAt = Objects.requireNonNull(finishedAt, "finishedAt must not be null");
    }

    // ---------- Factory Methods ----------

    /** Create a successful result with optional output and exit code. */
    public static ExecutionResult success(String output, int exitCode, Instant finishedAt) {
        return new ExecutionResult(true, output, null, exitCode, finishedAt);
    }

    /** Create a failure result with error message and exit code. */
    public static ExecutionResult failure(String errorMessage, int exitCode, Instant finishedAt) {
        return new ExecutionResult(false, null, errorMessage, exitCode, finishedAt);
    }


    public boolean isSuccess() { return success; }
    public String getOutput() { return output; }
    public String getErrorMessage() { return errorMessage; }
    public int getExitCode() { return exitCode; }
    public Instant getFinishedAt() { return finishedAt; }


    public boolean hasOutput() { return output != null && !output.isBlank(); }
    public boolean hasError() { return errorMessage != null && !errorMessage.isBlank(); }

    // ---------- Equality & HashCode ----------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecutionResult)) return false;
        ExecutionResult that = (ExecutionResult) o;
        return success == that.success &&
                exitCode == that.exitCode &&
                Objects.equals(output, that.output) &&
                Objects.equals(errorMessage, that.errorMessage) &&
                finishedAt.equals(that.finishedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, output, errorMessage, exitCode, finishedAt);
    }

    @Override
    public String toString() {
        return "ExecutionResult{" +
                "success=" + success +
                ", exitCode=" + exitCode +
                ", finishedAt=" + finishedAt +
                (success ? ", output='" + output + '\'' : ", error='" + errorMessage + '\'') +
                '}';
    }
}
