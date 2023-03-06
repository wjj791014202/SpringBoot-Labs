package com.base.facade;


import com.base.result.GenericPageResult;

public interface SupportPageCallback<T, B> {
    default void initContext() {
    }

    B getBizType();

    GenericPageResult<T> process(B bizType) throws Throwable;

    default void buildResult(final GenericPageResult<T> result) {
    }
}
