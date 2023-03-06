package com.rediscache;

import com.kucoin.starter.rediscache.invocation.InvocationCacheSupport;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ClassUtils;

@Aspect
public class AutoRefreshCacheAspect {
    @Autowired
    private InvocationCacheSupport invocationCacheSupport;
    private ExpressionParser parser = new SpelExpressionParser();
    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    public AutoRefreshCacheAspect() {
    }

    @Pointcut("@annotation(org.springframework.cache.annotation.Cacheable)")
    public void cacheablePointcut() {
    }

    @Around("cacheablePointcut()")
    public Object methodsAnnotatedWithCachealbe(final ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Method annotatedElement = this.getSpecificmethod(joinPoint);
            Cacheable cacheableAnno = (Cacheable)annotatedElement.getAnnotation(Cacheable.class);
            if (cacheableAnno != null) {
                Set<String> cacheSet = new HashSet();
                cacheSet.addAll(Arrays.asList(cacheableAnno.value()));
                String cacheKey = cacheableAnno.key();
                if (StringUtils.isNotEmpty(cacheKey)) {
                    cacheKey = this.parse(cacheKey, annotatedElement, joinPoint.getArgs());
                }

                this.invocationCacheSupport.addInvocation(joinPoint.getTarget(), annotatedElement, joinPoint.getArgs(), cacheSet, cacheKey);
            }
        } catch (Exception var6) {
        }

        return joinPoint.proceed();
    }

    private Method getSpecificmethod(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature)pjp.getSignature();
        Method method = methodSignature.getMethod();
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(pjp.getTarget());
        if (targetClass == null && pjp.getTarget() != null) {
            targetClass = pjp.getTarget().getClass();
        }

        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        return specificMethod;
    }

    private String parse(String key, Method method, Object[] args) {
        try {
            String[] params = this.discoverer.getParameterNames(method);
            if (params == null) {
                return key;
            } else {
                EvaluationContext context = new StandardEvaluationContext();

                for(int i = 0; i < params.length; ++i) {
                    context.setVariable(params[i], args[i]);
                }

                return (String)this.parser.parseExpression(key).getValue(context, String.class);
            }
        } catch (Exception var7) {
            return key;
        }
    }
}
