package com.base.exception;

public class DataAccessException extends BaseException {
    public DataAccessException(String message, ErrorCode errorCode, Object... args) {
        super(message, errorCode, args);
    }

    public DataAccessException(String message, Throwable cause, ErrorCode errorCode, Object... args) {
        super(message, cause, errorCode, args);
    }

    public DataAccessException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}

