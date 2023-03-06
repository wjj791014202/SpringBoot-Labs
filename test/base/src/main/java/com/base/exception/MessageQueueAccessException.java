package com.base.exception;

public class MessageQueueAccessException extends BaseException {
    public MessageQueueAccessException(String message, ErrorCode errorCode) {
        super(message, errorCode, new Object[0]);
    }

    public MessageQueueAccessException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode, new Object[0]);
    }

    public MessageQueueAccessException(ErrorCode errorCode) {
        super(errorCode, new Object[0]);
    }
}