package com.base.facade;


import java.util.stream.Stream;

public abstract class StreamSupportBase<T> {
    Stream<T> stream;

    StreamSupportBase(Stream<T> stream) {
        this.stream = stream;
    }
}

