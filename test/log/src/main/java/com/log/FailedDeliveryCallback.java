package com.log;


public interface FailedDeliveryCallback<E> {
    void onFailedDelivery(E evt, Throwable throwable);
}