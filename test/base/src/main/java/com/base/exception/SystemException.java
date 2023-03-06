package com.base.exception;


public class SystemException extends BaseException {
    public SystemException(String message, ErrorCode errorCode, Object... args) {
        super(message, errorCode, args);
    }

    public SystemException(String message, Throwable cause, ErrorCode errorCode, Object... args) {
        super(message, cause, errorCode, args);
    }

    public SystemException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}
