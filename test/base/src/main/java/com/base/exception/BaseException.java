package com.base.exception;

public class BaseException extends RuntimeException {
    private ErrorCode errorCode;
    private Object[] args;

    public BaseException(String message, ErrorCode errorCode, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }

    public BaseException(String message, Throwable cause, ErrorCode errorCode, Object... args) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = args;
    }

    public BaseException(ErrorCode errorCode, Object... args) {
        this(errorCode.getDescription(), errorCode, args);
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public Object[] getArgs() {
        return this.args;
    }
}
