package com.airtribe.chronos.interfaces;

import java.time.Duration;
import java.util.Optional;

public interface IdempotencyStore {
    boolean start(String key, Duration ttl);  // returns true if caller is first
    void complete(String key);
    Optional<Object> getResponse(String key);
}