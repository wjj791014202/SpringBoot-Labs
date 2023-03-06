package com.log;


import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Configuration
public class LogInterceptor {
    private static final Logger ERR_LOG = LoggerFactory.getLogger("COMMON-ERROR");
    private static final String PARAM_PLACEHOLDER = "***";

    public LogInterceptor() {
    }

    @Pointcut("@annotation(com.kucoin.starter.log.LogAnnotation)")
    public void log() {
    }

    @Around("log()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = null;

        try {
            method = BeanUtils.getAnnotationedMethod(joinPoint, LogAnnotation.class);
        } catch (Exception var4) {
            LoggerUtil.error(ERR_LOG, "获取日志拦截方法异常.", var4, new Object[]{var4.getMessage()});
            throw var4;
        }

        Object result = null;
        if (null == method) {
            result = joinPoint.proceed();
        } else {
            result = this.logProcess(joinPoint, method);
        }

        return result;
    }

    private void assembleUserId(final Object obj, final LogInfo logInfo) {
        try {
            Method getMethod = obj.getClass().getMethod("getUserId");
            if (getMethod == null) {
                logInfo.setUserId("-");
            } else {
                Object userId = getMethod.invoke(obj);
                if (userId instanceof String) {
                    logInfo.setUserId((String)userId);
                }
            }
        } catch (Throwable var5) {
            LoggerUtil.debug(ERR_LOG, "补全userId异常,%s", new Object[]{var5.getMessage()});
        }

    }

    private Object logProcess(ProceedingJoinPoint joinPoint, Method method) throws Throwable {
        LogAnnotation la = (LogAnnotation)method.getAnnotation(LogAnnotation.class);
        LogInfo logInfo = null;

        try {
            logInfo = this.before(joinPoint, method, la);
        } catch (Throwable var17) {
            LoggerUtil.error(ERR_LOG, "日志前置处理异常", var17, new Object[]{var17.getMessage()});
        }

        Object result = null;

        try {
            result = joinPoint.proceed();
        } catch (Throwable var15) {
            logInfo.setExceptional(true);
            logInfo.setThrowable(var15);
            throw var15;
        } finally {
            try {
                this.after(method, la, logInfo, result);
            } catch (Throwable var14) {
                LoggerUtil.error(ERR_LOG, "日志后置处理异常", var14, new Object[]{var14.getMessage()});
            }

        }

        return result;
    }

    private LogInfo before(ProceedingJoinPoint joinPoint, Method method, LogAnnotation annotation) {
        LogInfo logInfo = new LogInfo();
        logInfo.setBizCode(annotation.name());
        logInfo.setClassName(method.getDeclaringClass().getSimpleName());
        logInfo.setMethodName(method.getName());
        logInfo.setStartTime(System.currentTimeMillis());
        if (annotation.keyArg() >= 0 && annotation.keyArg() < joinPoint.getArgs().length) {
            logInfo.setKeyProps(joinPoint.getArgs()[annotation.keyArg()]);
            if (null != logInfo.getKeyProps() && StringUtils.isNotBlank(annotation.keyProps())) {
                this.assembleKeyProps(joinPoint, annotation.keyArg(), annotation.keyProps(), logInfo);
                this.assembleUserId(joinPoint.getArgs()[annotation.keyArg()], logInfo);
            }
        }

        this.assembleInfoArgs(joinPoint, annotation.includeArgs(), annotation.excludeArgs(), logInfo);
        return logInfo;
    }

    private void assembleInfoArgs(ProceedingJoinPoint joinPoint, String includeArgs, String excludeArgs, final LogInfo logInfo) {
        Object[] args = joinPoint.getArgs();
        int len = args.length;
        Object[] printArgs = new Object[len];
        String[] exArgs;
        String[] var9;
        int var10;
        int var11;
        String exArg;
        int idx;
        if (!StringUtils.isBlank(includeArgs)) {
            Arrays.fill(printArgs, "***");
            exArgs = StringUtils.split(includeArgs, ',');
            var9 = exArgs;
            var10 = exArgs.length;

            for(var11 = 0; var11 < var10; ++var11) {
                exArg = var9[var11];
                idx = NumberUtils.toInt(exArg, -1);
                if (idx >= 0 && idx < len) {
                    printArgs[idx] = args[idx];
                }
            }
        } else {
            System.arraycopy(args, 0, printArgs, 0, args.length);
        }

        if (!StringUtils.isBlank(excludeArgs)) {
            exArgs = StringUtils.split(excludeArgs, ',');
            var9 = exArgs;
            var10 = exArgs.length;

            for(var11 = 0; var11 < var10; ++var11) {
                exArg = var9[var11];
                idx = NumberUtils.toInt(exArg, -1);
                if (idx >= 0 && idx < len) {
                    printArgs[idx] = "***";
                }
            }
        }

        logInfo.setArgs(printArgs);
    }

    private void assembleKeyProps(final ProceedingJoinPoint joinPoint, final short keyArg, final String keyProps, final LogInfo logInfo) {
        String[] keyPropNames = StringUtils.split(keyProps, '|');

        for(int i = 0; i < keyPropNames.length; ++i) {
            String keyPropName = keyPropNames[i];

            try {
                Object keyPropsResult = null;
                Method getter = joinPoint.getArgs()[keyArg].getClass().getMethod("get" + keyPropName.substring(0, 1).toUpperCase() + keyPropName.substring(1));
                keyPropsResult = getter.invoke(joinPoint.getArgs()[keyArg]);
                if (i == 0) {
                    logInfo.setKeyProps("" + keyPropsResult);
                } else {
                    logInfo.setKeyProps(logInfo.getKeyProps() + "," + keyPropsResult);
                }
            } catch (Exception var10) {
                LoggerUtil.warn(ERR_LOG, "日志打印获取keyPropName=" + keyPropName + "值异常，请检查声明", var10, new Object[0]);
            }
        }

    }

    private void after(Method method, LogAnnotation annotation, LogInfo logInfo, Object result) {
        logInfo.setMethodName(method.getName());
        logInfo.setEndTime(System.currentTimeMillis());
        logInfo.setResult(result);
        if (StringUtils.isNotBlank(annotation.digestLogType())) {
            LoggerUtil.info(this.getLogger(annotation.digestLogType()), logInfo.printDigestLog());
        }

        if (StringUtils.isNotBlank(annotation.detailLogType())) {
            LoggerUtil.info(this.getLogger(annotation.detailLogType()), logInfo.printInfoLog());
        }

    }

    private Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
}

