package com.base.facade;


import com.base.exception.BaseException;
import com.base.exception.DataAccessException;
import com.base.result.CommonCode;
import com.base.result.GenericPageResult;
import com.base.result.GenericResult;
import com.base.util.ResultUtil;

public class FacadeSupport {

    public FacadeSupport() {
    }

    public static <T, B> GenericResult<T> process(final SupportCallback<T, B> callBack) {
        GenericResult<T> result = new GenericResult();
        Object bizType = callBack.getBizType();

        try {
            callBack.initContext();
            T t = callBack.process((B) bizType);
            result.setCode(String.valueOf(CommonCode.CODE_SUCCESS.code()));
            result.setMsg(CommonCode.CODE_SUCCESS.msg());
            result.setData(t);
            callBack.buildResult(result);
            result.setSuccess(true);
        } catch (BaseException var9) {
            ResultUtil.buildFailureResult(result, var9);
        } catch (Throwable var11) {
            ResultUtil.buildFailureResult(result, CommonCode.CODE_SERVER_ERROR);
        } finally {
            ContextDataHolder.clean();
        }

        return result;
    }

    public static <T, B> GenericPageResult<T> processList(final SupportPageCallback<T, B> callBack) {
        GenericPageResult<T> pageResult = new GenericPageResult();
        Object bizType = callBack.getBizType();

        try {
            callBack.initContext();
            GenericPageResult<T> result = callBack.process((B) bizType);
            pageResult = result;
            result.setCode(String.valueOf(CommonCode.CODE_SUCCESS.code()));
            result.setMsg(CommonCode.CODE_SUCCESS.msg());
            callBack.buildResult(result);
            result.setSuccess(true);
        } catch (BaseException var9) {
            ResultUtil.buildFailureResult(pageResult, var9);
        } catch (Throwable var11) {
            ResultUtil.buildFailureResult(pageResult, CommonCode.CODE_SERVER_ERROR);
        } finally {
            ContextDataHolder.clean();
        }

        return pageResult;
    }
}

