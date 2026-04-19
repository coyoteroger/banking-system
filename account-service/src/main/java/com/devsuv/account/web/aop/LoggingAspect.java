package com.devsuv.account.web.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("within(com.devsuv.account.web.controller..*)")
    public void logBefore(JoinPoint joinPoint) {
        log.info(">> Method: {} | Args: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "within(com.devsuv.account.web.controller..*)", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        log.info("<< Method: {} | Result: {}", joinPoint.getSignature().toShortString(), result);
    }
}
