package com.airtribe.chronos.interfaces;

import com.airtribe.chronos.entities.Execution;

public interface OutboxPublisher {

    void publishExecutionReady(Execution exec);
    void publish(Object event);
}
