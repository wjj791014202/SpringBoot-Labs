package com.base.facade;

import com.base.result.GenericResult;

public interface SupportCallback<T, B> {
    default void initContext() {
    }

    B getBizType();

    T process(B xbizType) throws Throwable;

    default void buildResult(final GenericResult<T> result) {
    }
}
