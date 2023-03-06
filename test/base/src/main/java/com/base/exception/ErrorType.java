package com.base.exception;
public enum ErrorType {
    PARAMETER,
    BIZ,
    SYSTEM,
    DB,
    CACHE,
    MQ;

    private ErrorType() {
    }
}
