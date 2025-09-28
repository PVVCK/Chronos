package com.airtribe.chronos.interfaces;

import com.airtribe.chronos.enums.JobType;

public interface JobExecutorFactory {

    JobExecutor getExecutor(JobType jobType);
}
