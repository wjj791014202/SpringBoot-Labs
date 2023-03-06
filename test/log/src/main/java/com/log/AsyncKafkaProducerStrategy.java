package com.log;


import ch.qos.logback.core.spi.ContextAwareBase;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class AsyncKafkaProducerStrategy extends ContextAwareBase implements DeliveryStrategy, Runnable {
    private LinkedBlockingQueue<AsyncKafkaProducerStrategy.SendBlock> queue = new LinkedBlockingQueue(32);
    private boolean running = true;
    private Thread thread;

    public AsyncKafkaProducerStrategy() {
    }

    public void start() {
        this.thread = new Thread(this, "AsyncKafkaProducer");
        this.thread.setDaemon(true);
        this.thread.setPriority(1);
        this.thread.start();
    }

    public void run() {
        while(true) {
            if (this.running) {
                try {
                    AsyncKafkaProducerStrategy.SendBlock block = (AsyncKafkaProducerStrategy.SendBlock)this.queue.take();
                    this.sendBlock(block);
                    continue;
                } catch (InterruptedException var2) {
                    this.addInfo("interrupted. exiting");
                    this.running = false;
                }
            }

            this.queue.clear();
            return;
        }
    }

    private void sendBlock(AsyncKafkaProducerStrategy.SendBlock block) {
        try {
            block.send();
        } catch (Exception var3) {
            this.addWarn("kafka log error", var3);
        }

    }

    public void exit() {
        if (this.thread != null && this.thread.isAlive() && !this.thread.isInterrupted()) {
            this.thread.interrupt();
        }

        this.running = false;
    }

    public <K, V, E> boolean send(Producer<K, V> producer, ProducerRecord<K, V> record, E event, FailedDeliveryCallback<E> failedDeliveryCallback) {
        AsyncKafkaProducerStrategy.SendBlock<K, V, E> block = new AsyncKafkaProducerStrategy.SendBlock(producer, record, event, failedDeliveryCallback);
        return this.queue.offer(block);
    }

    private static class SendBlock<K, V, E> {
        Producer<K, V> producer;
        ProducerRecord<K, V> record;
        E event;
        FailedDeliveryCallback<E> failedDeliveryCallback;

        SendBlock(Producer<K, V> producer, ProducerRecord<K, V> record, E event, FailedDeliveryCallback<E> failedDeliveryCallback) {
            this.producer = producer;
            this.record = record;
            this.event = event;
            this.failedDeliveryCallback = failedDeliveryCallback;
        }

        void send() {
            this.producer.send(this.record, new Callback() {
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null && SendBlock.this.failedDeliveryCallback != null) {
                        SendBlock.this.failedDeliveryCallback.onFailedDelivery(SendBlock.this.event, exception);
                    }

                }
            });
        }
    }
}

