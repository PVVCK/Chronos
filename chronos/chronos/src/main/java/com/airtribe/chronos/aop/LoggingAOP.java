package com.airtribe.chronos.aop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAOP {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAOP.class);
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ThreadLocal<Integer> callDepth = ThreadLocal.withInitial(() -> 0);

    @Around("execution(* com.airtribe.chronos..*(..)) "
            + "&& !execution(* com.airtribe.chronos.aop..*(..)) "
//            + "&& !execution(* com.airtribe.chronos.config..*(..)) "
//            + "&& !execution(* com.airtribe.chronos.security..*(..))"
    )
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        if (callDepth.get() > 10) {
            logger.warn("Skipping execution to prevent recursion: {}", joinPoint.getSignature().toShortString());
            return null;
        }

        callDepth.set(callDepth.get() + 1);
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String methodName = joinPoint.getSignature().toShortString();

        logger.info("[{}] Entering method: {} with arguments: {}", timestamp, methodName, Arrays.toString(joinPoint.getArgs()));

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            result = joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            logger.info("[{}] Exiting method: {}. Execution time: {} ms", timestamp, methodName, executionTime);
            callDepth.set(callDepth.get() - 1);
        }

        return result;
    }
}

