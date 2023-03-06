package com.base.facade;


import com.base.result.GenericPageResult;

public interface PageProcessor<T, R, B> {
    GenericPageResult<R> process(B bizType, T request);
}

