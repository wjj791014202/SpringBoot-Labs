package com.log;

public interface KeyingStrategy<E> {
    byte[] createKey(E e);
}
