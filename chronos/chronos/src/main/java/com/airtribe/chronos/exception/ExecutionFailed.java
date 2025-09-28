package com.airtribe.chronos.exception;

public class ExecutionFailed extends RuntimeException {
    public ExecutionFailed(String message) {

        super(message);
    }

    public ExecutionFailed(String message, Throwable cause) {
        super(message, cause);
    }
}
