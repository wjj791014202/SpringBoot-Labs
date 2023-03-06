package com.base.exception;


public class BizException extends BaseException {
    public BizException(String message, ErrorCode errorCode, Object... args) {
        super(message, errorCode, args);
    }

    public BizException(String message, Throwable cause, ErrorCode errorCode, Object... args) {
        super(message, cause, errorCode, args);
    }

    public BizException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}

