package com.base.exception;


public enum CommonErrorCode implements ErrorCode {
    SYSTEM_EXCEPTION(ErrorType.SYSTEM, ErrorLevel.ERROR, "0000000", true, "system exception"),
    DB_EXCEPTION(ErrorType.DB, ErrorLevel.ERROR, "0000001", true, "db exception"),
    CACHE_EXCEPTION(ErrorType.CACHE, ErrorLevel.ERROR, "0000002", true, "cache exception"),
    MQ_EXCEPTION(ErrorType.MQ, ErrorLevel.ERROR, "0000003", true, "message queue exception"),
    INNER_ERROR(ErrorType.BIZ, ErrorLevel.ERROR, "100000", false, "inner check error"),
    BIZ_ERROR(ErrorType.BIZ, ErrorLevel.INFO, "200000", false, "inner check error"),
    IDEMPOTENT_ERROR(ErrorType.BIZ, ErrorLevel.INFO, "200001", false, "idempotent error"),
    PARAMETER_ILLEGAL(ErrorType.PARAMETER, ErrorLevel.ERROR, "300000", false, "request parameter illegal"),
    NOT_FOUND(ErrorType.BIZ, ErrorLevel.INFO, "404", false, "Not Found"),
    RATE_LIMITED(ErrorType.BIZ, ErrorLevel.WARN, "200002", true, "rate limited");

    private final ErrorType errorType;
    private final ErrorLevel errorLevel;
    private final String code;
    private final String description;
    private boolean retry;

    private CommonErrorCode(ErrorType errorType, ErrorLevel errorLevel, String code, boolean retry, String description) {
        this.errorLevel = errorLevel;
        this.errorType = errorType;
        this.code = code;
        this.retry = retry;
        this.description = description;
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }

    public ErrorLevel getErrorLevel() {
        return this.errorLevel;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isRetry() {
        return this.retry;
    }

}

