package com.metrics;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author wangjijiang
 * @date 2021/4/13 11:14
 */
@Aspect
@Component
public class MetricsAspect {
    @Around("@annotation(with)")
    public Object metricsAspect(ProceedingJoinPoint point, MetricsWith with) throws Throwable {
        String name=with.value();
        try(TimerMetric m=Metrics.measureTime(name)){
            return point.proceed();
        }
    }
}
