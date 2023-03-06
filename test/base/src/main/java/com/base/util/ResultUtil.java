package com.base.util;


import com.base.exception.BaseException;
import com.base.exception.ErrorCode;
import com.base.exception.ErrorLevel;
import com.base.exception.ErrorType;
import com.base.result.AbstractResult;
import com.base.result.CommonCode;
import com.base.result.ResultCode;
import org.apache.commons.lang3.StringUtils;

public class ResultUtil {
    public ResultUtil() {
    }

    public static void buildFailureResult(final AbstractResult result, final BaseException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        if (errorCode != null && errorCode.getErrorType() == ErrorType.BIZ) {
            result.setCode(errorCode.getCode());
            result.setMsg(exception.getMessage());
        } else if (errorCode != null && errorCode.getErrorType() == ErrorType.PARAMETER) {
            result.setCode(String.valueOf(CommonCode.CODE_PARAM_ERROR.code()));
            if (StringUtils.isBlank(exception.getMessage())) {
                result.setMsg(CommonCode.CODE_PARAM_ERROR.msg());
            } else {
                result.setMsg(exception.getMessage());
            }
        } else {
            result.setCode(String.valueOf(CommonCode.CODE_SERVER_ERROR));
            result.setMsg(CommonCode.CODE_SERVER_ERROR.msg());
        }

        result.setRetry(errorCode != null && errorCode.isRetry());
        result.setArgs(exception.getArgs());
        result.setSuccess(false);
    }

    public static void buildFailureResult(final AbstractResult result, final ResultCode errorCode) {
        result.setCode(String.valueOf(errorCode.code()));
        result.setMsg(errorCode.msg());
        result.setSuccess(false);
    }

    public static void logBizError(BaseException e, Logger logger, Object bizType) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorLevel level = errorCode == null ? ErrorLevel.WARN : errorCode.getErrorLevel();
        if (level == null) {
            level = ErrorLevel.WARN;
        }

        switch(level) {
            case INFO:
                logger.info("Biz process exception. bizType:{} errorCode:{} msg:{}", new Object[]{bizType, e.getErrorCode(), e.getMessage()});
                break;
            case WARN:
                logger.warn("Biz process exception. bizType:" + bizType, e);
                break;
            case ERROR:
                logger.error("Biz process exception. bizType:" + bizType, e);
        }

    }
}

