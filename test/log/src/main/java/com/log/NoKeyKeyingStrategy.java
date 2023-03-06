package com.log;


public class NoKeyKeyingStrategy implements KeyingStrategy<Object> {
    public NoKeyKeyingStrategy() {
    }

    public byte[] createKey(Object e) {
        return null;
    }
}
