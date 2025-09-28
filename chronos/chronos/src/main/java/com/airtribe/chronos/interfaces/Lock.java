package com.airtribe.chronos.interfaces;


import java.time.Instant;

public interface Lock {
    void release();
    String ownerId();
    Instant expiry();
}

