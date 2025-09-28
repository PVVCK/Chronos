package com.airtribe.chronos.interfaces;

import com.airtribe.chronos.entities.RetryPolicy;
import com.airtribe.chronos.enums.RetryStrategyKey;

import java.time.Duration;

public interface RetryStrategy {

    Duration computeDelay(int attemptNo, RetryPolicy policy);
    RetryStrategyKey key();
}
