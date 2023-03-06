package com.log;


import com.kucoin.base.result.GenericResult;

public class BaseResultParser implements LogResultParser {
    private static final String NONE = "-";

    public BaseResultParser() {
    }

    public boolean isSuccess(Object result) {
        return result instanceof GenericResult ? ((GenericResult)result).isSuccess() : true;
    }

    public String getErrorCode(Object result) {
        return result instanceof GenericResult ? ((GenericResult)result).getCode() : "-";
    }
}
