package com.airtribe.chronos.interfaces;

import com.airtribe.chronos.entities.Job;
import com.airtribe.chronos.exceptions.JobExecutionException;
import com.airtribe.chronos.valueobjects.ExecutionContext;
import com.airtribe.chronos.valueobjects.ExecutionResult;

public interface JobExecutor {
    ExecutionResult execute(Job job, ExecutionContext ctx) throws JobExecutionException;
}
