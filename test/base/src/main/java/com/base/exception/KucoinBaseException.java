package com.base.exception;


public class KucoinBaseException extends RuntimeException {
    private String errorCode = "DEFAULT";

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public KucoinBaseException(String message) {
        super(message);
    }

    public KucoinBaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

