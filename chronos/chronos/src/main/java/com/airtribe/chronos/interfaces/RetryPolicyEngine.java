package com.airtribe.chronos.interfaces;

import com.airtribe.chronos.entities.RetryPolicy;

import java.time.Duration;

public interface RetryPolicyEngine {

    Duration nextDelay(int attempt, RetryPolicy policy);
}
