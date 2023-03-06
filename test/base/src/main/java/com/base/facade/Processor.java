package com.base.facade;


public interface Processor<T, R, B> {
    R process(B bizType, T t);
}
