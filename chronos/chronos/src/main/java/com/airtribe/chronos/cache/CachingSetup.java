package com.airtribe.chronos.cache;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



import jakarta.transaction.Transactional;

@Component
public class CachingSetup {

    private static final Logger logInfo = LoggerFactory.getLogger(CachingSetup.class);
    private final CacheManager cacheManager;

    private final ReentrantLock cacheClearLock = new ReentrantLock();

    public CachingSetup(CacheManager cacheManager)
    {
        this.cacheManager = cacheManager;

    }

    @Scheduled(fixedRate = 9000000, initialDelay = 1500000)
    @Transactional
    @Async
    public void clearAllCaches()
    {
        boolean acquiredLock = false;
        List<String> clearCaches = new ArrayList<>();

        try
        {
            acquiredLock = cacheClearLock.tryLock();
            if(acquiredLock)
            {
                cacheManager.getCacheNames().forEach(cacheName -> {
                    Cache cache = cacheManager.getCache(cacheName);
                    if(cache!=null)
                    {
                        cache.clear();
                        clearCaches.add(cacheName);
                    }
                });

                if(!clearCaches.isEmpty())
                {
                    logInfo.info("Cleared following Cache's :- " + String.join(", ",clearCaches));
                }
            }
            else
            {
                logInfo.info("Another Cache Clear operation is already in progress.");
            }
        }

        finally
        {
            if(acquiredLock)
                cacheClearLock.unlock();
        }
    }

    @Transactional
    @Async
    public void clearCacheByNames(List<String> cacheNames)
    {
        boolean acquiredLock = false;
        try
        {
            acquiredLock = cacheClearLock.tryLock();
            if(acquiredLock)
            {
                Collection<String> allCacheNames = cacheManager.getCacheNames();
                cacheNames.forEach(name -> {
                    if(allCacheNames.contains(name))
                    {
                        Cache cache = cacheManager.getCache(name);
                        if(cache!=null)
                        {
                            cache.clear();
                            logInfo.info("Cache " + name + " cleared");
                        }
                    }
                });
            }

            else
            {
                logInfo.info("Another Cache Clear operation is already in progress.");
            }
        }

        finally
        {
            if(acquiredLock)
                cacheClearLock.unlock();
        }
    }


    @Transactional
    @Async
    public void clearCacheByNames(Map<String, String> cacheKeys) {
        boolean acquiredLock = false;
        try {
            acquiredLock = cacheClearLock.tryLock();
            if (acquiredLock) {
                Collection<String> allCacheNames = cacheManager.getCacheNames();

                cacheKeys.forEach((cacheName, cacheKey) -> {
                    if (allCacheNames.contains(cacheName)) {
                        Cache cache = cacheManager.getCache(cacheName);
                        if (cache != null) {
                            cache.evict(cacheKey);
                            logInfo.info("Cache " + cacheName + " cleared for key: " + cacheKey);
                        }
                    }
                });
            } else {
                logInfo.info("Another Cache Clear operation is already in progress.");
            }
        } finally {
            if (acquiredLock) {
                cacheClearLock.unlock();
            }
        }
    }

}


