package com.log;


public interface LogResultParser {
    boolean isSuccess(Object result);

    String getErrorCode(Object result);
}
