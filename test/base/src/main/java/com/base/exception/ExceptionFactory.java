package com.base.exception;


import java.util.HashMap;
import java.util.Map;

public class ExceptionFactory {
    private static final Map<ErrorType, Class<? extends BaseException>> MAPPER = new HashMap<ErrorType, Class<? extends BaseException>>() {
        {
            this.put(ErrorType.SYSTEM, BaseException.class);
            this.put(ErrorType.DB, DataAccessException.class);
            this.put(ErrorType.CACHE, CacheAccessException.class);
            this.put(ErrorType.MQ, MessageQueueAccessException.class);
            this.put(ErrorType.PARAMETER, ParameterException.class);
            this.put(ErrorType.BIZ, BizException.class);
        }
    };

    private ExceptionFactory() {
    }

    public static ExceptionFactory getInstance() {
        return ExceptionFactory.ExceptionFactoryHolder.INSTANCE;
    }

    public static BaseException create(String message, ErrorCode errorCode, Object... args) {
        try {
            return (BaseException)((Class)MAPPER.get(errorCode.getErrorType())).getDeclaredConstructor(String.class, ErrorCode.class, Object[].class).newInstance(message, errorCode, args);
        } catch (Exception var4) {
            return new BaseException(message, errorCode, args);
        }
    }

    public static BaseException create(String message, Throwable caused, ErrorCode errorCode, Object... args) {
        try {
            return (BaseException)((Class)MAPPER.get(errorCode.getErrorType())).getDeclaredConstructor(String.class, Throwable.class, ErrorCode.class, Object[].class).newInstance(message, caused, errorCode, args);
        } catch (Exception var5) {
            return new BaseException(message, caused, errorCode, args);
        }
    }

    public static BaseException create(ErrorCode errorCode, Object... args) {
        try {
            return (BaseException)((Class)MAPPER.get(errorCode.getErrorType())).getDeclaredConstructor(ErrorCode.class, Object[].class).newInstance(errorCode, args);
        } catch (Exception var3) {
            return new BaseException(errorCode, args);
        }
    }

    private static class ExceptionFactoryHolder {
        static ExceptionFactory INSTANCE = new ExceptionFactory();

        private ExceptionFactoryHolder() {
        }
    }
}

