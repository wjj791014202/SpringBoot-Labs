package com.base.result;

import com.kucoin.base.model.Pagination;
import java.util.List;

public class GenericPageResult<T> extends AbstractResult {
    private static final long serialVersionUID = 1L;
    private Integer currentPage;
    private Integer pageSize;
    private Long totalNum;
    private Integer totalPage;
    private List<T> items;

    public GenericPageResult() {
    }

    public static <T> GenericPageResult<T> fail(ResultCode code, String... msg) {
        GenericPageResult<T> pageResult = new GenericPageResult();
        pageResult.setSuccess(false);
        pageResult.setCode(String.valueOf(code.code()));
        pageResult.setMsg(msg.length == 0 ? code.msg() : msg[0]);
        pageResult.setRetry(true);
        return pageResult;
    }

    public static <T> GenericPageResult<T> fail(boolean retry, ResultCode code, String... msg) {
        GenericPageResult<T> pageResult = new GenericPageResult();
        pageResult.setSuccess(false);
        pageResult.setCode(String.valueOf(code.code()));
        pageResult.setMsg(msg.length == 0 ? code.msg() : msg[0]);
        pageResult.setRetry(retry);
        return pageResult;
    }

    public static <T> GenericPageResult<T> fail(String code, String... msg) {
        GenericPageResult<T> pageResult = new GenericPageResult();
        pageResult.setSuccess(false);
        pageResult.setCode(code);
        pageResult.setMsg(msg.length == 0 ? null : msg[0]);
        return pageResult;
    }

    public static <T> GenericPageResult<T> success(Pagination<T> page) {
        GenericPageResult<T> pageResult = new GenericPageResult();
        pageResult.setSuccess(true);
        pageResult.setCode(String.valueOf(CommonCode.CODE_SUCCESS.code()));
        pageResult.setMsg(CommonCode.CODE_SUCCESS.msg());
        pageResult.setCurrentPage(page.getCurrentPage());
        pageResult.setPageSize(page.getPageSize());
        pageResult.setTotalNum(page.getTotalNum());
        pageResult.setItems(page.getItems());
        pageResult.setTotalPage(page.getTotalPage());
        return pageResult;
    }

    public static <T> GenericPageResult<T> success(Integer currentPage, Integer pageSize, Long totalNum, List<T> items) {
        GenericPageResult<T> pageResult = new GenericPageResult();
        pageResult.setSuccess(true);
        pageResult.setCode(String.valueOf(CommonCode.CODE_SUCCESS.code()));
        pageResult.setMsg(CommonCode.CODE_SUCCESS.msg());
        pageResult.setCurrentPage(currentPage);
        pageResult.setPageSize(pageSize);
        pageResult.setTotalNum(totalNum);
        pageResult.setItems(items);
        pageResult.setTotalPage((int)((totalNum + (long)pageSize - 1L) / (long)pageSize));
        return pageResult;
    }

    public Integer getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalNum() {
        return this.totalNum;
    }

    public void setTotalNum(Long totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getItems() {
        return this.items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}

