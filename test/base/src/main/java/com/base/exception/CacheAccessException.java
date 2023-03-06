package com.base.exception;

public class CacheAccessException extends BaseException {
    public CacheAccessException(String message, ErrorCode errorCode, Object... args) {
        super(message, errorCode, args);
    }

    public CacheAccessException(String message, Throwable cause, ErrorCode errorCode, Object... args) {
        super(message, cause, errorCode, args);
    }

    public CacheAccessException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}