package com.base.facade;


import java.util.function.Function;
import java.util.stream.Stream;

public class FacadeStreamSupport<T> extends StreamSupportBase<T> {
    FacadeStreamSupport(Stream<T> stream) {
        super(stream);
    }

    public static <T> FacadeStreamSupport<T> of(T request) {
        return new FacadeStreamSupport(Stream.of(request));
    }

    public static <T> FacadeStreamSupport<T> of() {
        return new FacadeStreamSupport(Stream.of(new Object()));
    }

    public <R> FacadeStreamSupport<R> map(Function<? super T, ? extends R> mapper) {
        return new FacadeStreamSupport(this.stream.map(mapper));
    }

    public <R> FacadeStreamSupport<R> convert(Function<? super T, ? extends R> mapper) {
        return new FacadeStreamSupport(this.stream.map(mapper));
    }

    public <B> FacadeBizTypeStreamSupport<T, B> bizType(B bizType) {
        return new FacadeBizTypeStreamSupport(this.stream, bizType);
    }
}

