package com.log;

import org.apache.kafka.clients.producer.BufferExhaustedException;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.TimeoutException;

public class AsynchronousDeliveryStrategy implements DeliveryStrategy {
    public AsynchronousDeliveryStrategy() {
    }

    public <K, V, E> boolean send(Producer<K, V> producer, ProducerRecord<K, V> record, final E event, final FailedDeliveryCallback<E> failedDeliveryCallback) {
        try {
            producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        failedDeliveryCallback.onFailedDelivery(event, exception);
                    }

                }
            });
            return true;
        } catch (TimeoutException | BufferExhaustedException var6) {
            failedDeliveryCallback.onFailedDelivery(event, var6);
            return false;
        }
    }
}
