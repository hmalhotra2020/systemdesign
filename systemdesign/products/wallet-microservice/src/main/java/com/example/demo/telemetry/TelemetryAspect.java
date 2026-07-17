package com.example.demo.telemetry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.StringJoiner;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TelemetryAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryAspect.class);

    @Around("within(com.example.demo.controller..*) || within(com.example.demo.service..*)")
    public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
        String signature = joinPoint.getSignature().toShortString();
        
        // Resolve method reflection to look for ExcludeTelemetry annotation
        boolean excludeMethod = false;
        Annotation[][] parameterAnnotations = new Annotation[0][0];
        
        if (joinPoint.getSignature() instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            excludeMethod = method.isAnnotationPresent(ExcludeTelemetry.class);
            parameterAnnotations = method.getParameterAnnotations();
        }
        
        String argsSummary;
        if (excludeMethod) {
            argsSummary = "[REDACTED]";
        } else {
            Object[] args = joinPoint.getArgs();
            StringJoiner joiner = new StringJoiner(", ");
            for (int i = 0; i < args.length; i++) {
                boolean sensitive = false;
                if (i < parameterAnnotations.length) {
                    for (Annotation ann : parameterAnnotations[i]) {
                        if (ann instanceof ExcludeTelemetry) {
                            sensitive = true;
                            break;
                        }
                    }
                }
                if (sensitive) {
                    joiner.add("[REDACTED]");
                } else {
                    joiner.add(String.valueOf(args[i]));
                }
            }
            argsSummary = joiner.toString();
        }

        long start = System.nanoTime();
        LOGGER.info("Executing {} with args [{}]", signature, argsSummary);
        try {
            Object result = joinPoint.proceed();
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            // Risk mitigation: Log method completion and duration, but omit logging the return result object itself
            LOGGER.info("Completed {} successfully in {} ms", signature, durationMs);
            return result;
        } catch (Throwable ex) {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            LOGGER.warn("Failed {} in {} ms due to {}", signature, durationMs, ex.getMessage());
            throw ex;
        }
    }
}
