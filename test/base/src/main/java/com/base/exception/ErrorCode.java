package com.base.exception;


public interface ErrorCode {
    ErrorType getErrorType();

    ErrorLevel getErrorLevel();

    String getCode();

    String getDescription();

    boolean isRetry();
}
