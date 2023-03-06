package com.base.exception;


public class ParameterException extends BaseException {
    public ParameterException(String message, ErrorCode errorCode) {
        super(message, errorCode, new Object[0]);
    }

    public ParameterException(String message, ErrorCode errorCode, Object... args) {
        super(message, errorCode, args);
    }

    public ParameterException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode, new Object[0]);
    }

    public ParameterException(ErrorCode errorCode) {
        super(errorCode, new Object[0]);
    }
}

