package com.base.model;

import java.util.List;

public class Pagination<T> {
    private Integer currentPage;
    private Integer pageSize;
    private Long totalNum;
    private Integer totalPage;
    private List<T> items;

    public Pagination() {
    }

    public Pagination(Integer currentPage, Integer pageSize, Long totalNum, List<T> items) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalNum = totalNum;
        this.items = items;
        this.totalPage = (int)((totalNum + (long)pageSize - 1L) / (long)pageSize);
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

