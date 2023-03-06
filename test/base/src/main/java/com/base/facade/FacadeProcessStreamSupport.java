package com.base.facade;


import com.base.exception.BaseException;
import com.base.exception.CommonErrorCode;
import com.base.exception.DataAccessException;
import com.base.exception.ErrorCode;
import com.base.result.CommonCode;
import com.base.result.GenericListResult;
import com.base.result.GenericPageResult;
import com.base.result.GenericResult;
import com.base.util.ResultUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FacadeProcessStreamSupport<T, B> extends StreamSupportBase<T> {

    private boolean isThrowNull;
    private ErrorCode errorCode;
    private B bizType;

    FacadeProcessStreamSupport(Stream<T> stream, B bizType) {
        super(stream);
        this.bizType = bizType;
    }

    public <Z, R> Z process(Processor<T, R, B> processor, Function<? super R, ? extends Z> mapper) {
        Object var3;
        try {
            var3 = mapper == null ? this.processInternal(processor) : this.processInternal(processor, mapper);
        } catch (DataAccessException var7) {
            throw new DataAccessException("processor catch db error", var7, CommonErrorCode.DB_EXCEPTION, new Object[0]);
        } finally {
            ContextDataHolder.clean();
        }

        return var3;
    }

    public <R> R process(Processor<T, R, B> processor) {
        return this.process(processor, (Function) null);
    }

    private <Z, R> List<Z> processInternalList(Processor<T, List<R>, B> processor, Function<? super R, ? extends Z> mapper) {
        return (List) this.stream.map((t) -> {
            return (List) processor.process(this.bizType, t);
        }).map((rs) -> {
            if (rs == null) {
                return null;
            } else {
                return mapper == null ? rs : (List) rs.stream().map(mapper).collect(Collectors.toList());
            }
        }).filter((r) -> {
            return r != null;
        }).findFirst().orElse((Object) null);
    }

    private <R> GenericPageResult<R> processInternalPage(PageProcessor<T, R, B> processor) {
        return (GenericPageResult) this.stream.map((t) -> {
            return processor.process(this.bizType, t);
        }).findFirst().orElse(GenericPageResult.fail(CommonCode.CODE_COMMON_ERROR, new String[0]));
    }

    public FacadeProcessStreamSupport<T, B> onNullThrow() {
        this.isThrowNull = true;
        return this;
    }

    public FacadeProcessStreamSupport<T, B> onNullThrow(ErrorCode errorCode) {
        this.isThrowNull = true;
        this.errorCode = errorCode;
        return this;
    }

    private <R> R processInternal(Processor<T, R, B> processor) {
        return this.stream.map((t) -> {
            return processor.process(this.bizType, t);
        }).filter((r) -> {
            return r != null;
        }).findFirst().orElse((Object) null);
    }

    private <Z, R> Z processInternal(Processor<T, R, B> processor, Function<? super R, ? extends Z> mapper) {
        return this.stream.map((t) -> {
            return processor.process(this.bizType, t);
        }).map(mapper).filter((r) -> {
            return r != null;
        }).findFirst().orElse((Object) null);
    }

    private <Z, R> GenericResult<Z> processResultInternal(Processor<T, R, B> processor, Function<? super R, ? extends Z> mapper, Consumer<GenericResult<Z>> customer) {
        GenericResult result = new GenericResult();

        try {
            result.setData(mapper == null ? this.processInternal(processor) : this.processInternal(processor, mapper));
            result.setSuccess(true);
            result.setCode(String.valueOf(CommonCode.CODE_SUCCESS.code()));
            result.setMsg(CommonCode.CODE_SUCCESS.msg());
            if (customer != null) {
                customer.accept(result);
            }
        } catch (BaseException var11) {
            ResultUtil.buildFailureResult(result, var11);
        } catch (DataAccessException var12) {
            ResultUtil.buildFailureResult(result, CommonCode.CODE_SERVER_ERROR);
        } catch (Throwable var13) {
            ResultUtil.buildFailureResult(result, CommonCode.CODE_SERVER_ERROR);
        } finally {
            ContextDataHolder.clean();
        }

        return result;
    }

    private <Z, R> GenericListResult<Z> processListResultInternal(Processor<T, List<R>, B> processor, Function<? super R, ? extends Z> mapper) {
        GenericListResult listResult = new GenericListResult();

        try {
            List<Z> items = this.processInternalList(processor, mapper);
            listResult.setItems(items);
            listResult.setSuccess(true);
            listResult.setCode(String.valueOf(CommonCode.CODE_SUCCESS.code()));
            listResult.setMsg(CommonCode.CODE_SUCCESS.msg());
        } catch (BaseException var10) {
            ResultUtil.logBizError(var10, logger, this.bizType);
            ResultUtil.buildFailureResult(listResult, var10);
        } catch (DataAccessException var11) {
            ResultUtil.buildFailureResult(listResult, CommonCode.CODE_SERVER_ERROR);
        } catch (Throwable var12) {
            ResultUtil.buildFailureResult(listResult, CommonCode.CODE_SERVER_ERROR);
        } finally {
            ContextDataHolder.clean();
        }

        return listResult;
    }

    public <R> GenericListResult<R> processListResult(Processor<T, List<R>, B> processor) {
        return this.processListResultInternal(processor, (Function) null);
    }

    public <Z, R> GenericListResult<Z> processListResult(Processor<T, List<R>, B> processor, Function<? super R, ? extends Z> mapper) {
        return this.processListResultInternal(processor, mapper);
    }

    public <R> GenericResult<R> processResult(Processor<T, R, B> processor) {
        return this.processResultInternal(processor, (Function) null, (Consumer) null);
    }

    public <Z, R> GenericResult<Z> processResult(Processor<T, R, B> processor, Function<? super R, ? extends Z> mapper) {
        return this.processResultInternal(processor, mapper, (Consumer) null);
    }

    public <Z, R> GenericResult<Z> processResult(Processor<T, R, B> processor, Function<? super R, ? extends Z> mapper, Consumer<GenericResult<Z>> customer) {
        return this.processResultInternal(processor, mapper, customer);
    }

    public <Z, R> GenericResult<Z> processResult(Processor<T, R, B> processor, Consumer<GenericResult<Z>> customer) {
        return this.processResultInternal(processor, (Function) null, customer);
    }

    private <Z, R> GenericPageResult<Z> processPageResultInternal(PageProcessor<T, R, B> processor, Function<R, Z> mapper) {
        GenericPageResult result = new GenericPageResult();

        try {
            GenericPageResult<R> processResult = this.processInternalPage(processor);
            List<R> items = processResult.getItems();
            if (mapper == null || items == null) {
                if (processResult.getCode() == null) {
                    processResult.setCode(String.valueOf(CommonCode.CODE_SUCCESS.code()));
                    processResult.setMsg(CommonCode.CODE_SUCCESS.msg());
                }

                processResult.setSuccess(true);
                GenericPageResult var18 = processResult;
                return var18;
            }

            result.setCurrentPage(processResult.getCurrentPage());
            result.setPageSize(processResult.getPageSize());
            result.setTotalNum(processResult.getTotalNum());
            result.setTotalPage(processResult.getTotalPage());
            List<Z> convertItems = new ArrayList(items.size());
            Iterator var7 = items.iterator();

            while (var7.hasNext()) {
                R item = var7.next();
                convertItems.add(mapper.apply(item));
            }

            result.setItems(convertItems);
            if (processResult.getCode() == null) {
                result.setCode(String.valueOf(CommonCode.CODE_SUCCESS.code()));
                result.setMsg(CommonCode.CODE_SUCCESS.msg());
            } else {
                result.setCode(processResult.getCode());
                result.setMsg(processResult.getMsg());
            }

            result.setSuccess(true);
        } catch (BaseException var14) {
            ResultUtil.buildFailureResult(result, var14);
        } catch (DataAccessException var15) {
            ResultUtil.buildFailureResult(result, CommonCode.CODE_SERVER_ERROR);
        } catch (Throwable var16) {
            ResultUtil.buildFailureResult(result, CommonCode.CODE_SERVER_ERROR);
        } finally {
            ContextDataHolder.clean();
        }

        return result;
    }

    public <R> GenericPageResult<R> processPageResult(PageProcessor<T, R, B> processor) {
        return this.processPageResultInternal(processor, (Function) null);
    }

    public <Z, R> GenericPageResult<Z> processPageResult(PageProcessor<T, R, B> processor, Function<R, Z> mapper) {
        return this.processPageResultInternal(processor, mapper);
    }
}

