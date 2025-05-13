package ord.example.LegalManagementSystem.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* ord.example.LegalManagementSystem.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Calling method: {}", joinPoint.getSignature().toShortString());
    }
}