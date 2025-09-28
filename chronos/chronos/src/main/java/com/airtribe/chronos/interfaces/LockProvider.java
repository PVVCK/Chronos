package com.airtribe.chronos.interfaces;

import java.time.Duration;
import java.util.Optional;


public interface LockProvider {

    Optional<Lock> tryAcquire(String key, Duration ttl);
}
