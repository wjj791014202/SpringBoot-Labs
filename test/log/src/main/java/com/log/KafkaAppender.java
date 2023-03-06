package com.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import ch.qos.logback.core.spi.FilterReply;
import com.kucoin.starter.log.appender.delivery.AsyncKafkaProducerStrategy;
import com.kucoin.starter.log.appender.delivery.FailedDeliveryCallback;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.ByteArraySerializer;

public class KafkaAppender<E> extends KafkaAppenderConfig<E> {
    private static final String KAFKA_LOGGER_PREFIX = KafkaProducer.class.getPackage().getName().replaceFirst("\\.producer$", "");
    private KafkaAppender<E>.LazyProducer lazyProducer = null;
    private final AppenderAttachableImpl<E> aai = new AppenderAttachableImpl();
    private final ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue();
    private final FailedDeliveryCallback<E> failedDeliveryCallback = new FailedDeliveryCallback<E>() {
        public void onFailedDelivery(E evt, Throwable throwable) {
            KafkaAppender.this.aai.appendLoopOnAppenders(evt);
        }
    };
    boolean initFailed = false;
    long initTime = 0L;

    public KafkaAppender() {
        this.addProducerConfigValue("key.serializer", ByteArraySerializer.class.getName());
        this.addProducerConfigValue("value.serializer", ByteArraySerializer.class.getName());
        this.addFilter(new Filter<E>() {
            public FilterReply decide(E event) {
                return KafkaAppender.this.initFailed && System.currentTimeMillis() - KafkaAppender.this.initTime < 300000L ? FilterReply.DENY : FilterReply.NEUTRAL;
            }
        });
    }

    public void doAppend(E e) {
        this.ensureDeferredAppends();
        if (e instanceof ILoggingEvent && ((ILoggingEvent)e).getLoggerName().startsWith(KAFKA_LOGGER_PREFIX)) {
            this.deferAppend(e);
        } else {
            super.doAppend(e);
        }

    }

    public void start() {
        if (this.checkPrerequisites()) {
            if (this.partition != null && this.partition < 0) {
                this.partition = null;
            }

            this.lazyProducer = new KafkaAppender.LazyProducer();
            super.start();
            if (this.deliveryStrategy instanceof AsyncKafkaProducerStrategy) {
                ((AsyncKafkaProducerStrategy)this.deliveryStrategy).start();
            }

        }
    }

    public void stop() {
        super.stop();
        if (this.lazyProducer != null && this.lazyProducer.isInitialized()) {
            try {
                this.lazyProducer.get().close();
            } catch (KafkaException var2) {
                this.addWarn("Failed to shut down kafka producer: " + var2.getMessage(), var2);
            }

            this.lazyProducer = null;
        }

        if (this.deliveryStrategy instanceof AsyncKafkaProducerStrategy) {
            ((AsyncKafkaProducerStrategy)this.deliveryStrategy).exit();
        }

    }

    public void addAppender(Appender<E> newAppender) {
        this.aai.addAppender(newAppender);
    }

    public Iterator<Appender<E>> iteratorForAppenders() {
        return this.aai.iteratorForAppenders();
    }

    public Appender<E> getAppender(String name) {
        return this.aai.getAppender(name);
    }

    public boolean isAttached(Appender<E> appender) {
        return this.aai.isAttached(appender);
    }

    public void detachAndStopAllAppenders() {
        this.aai.detachAndStopAllAppenders();
    }

    public boolean detachAppender(Appender<E> appender) {
        return this.aai.detachAppender(appender);
    }

    public boolean detachAppender(String name) {
        return this.aai.detachAppender(name);
    }

    protected void append(E e) {
        byte[] payload = this.encoder.encode(e);
        byte[] key = this.keyingStrategy.createKey(e);
        Long timestamp = this.isAppendTimestamp() ? this.getTimestamp(e) : null;
        ProducerRecord<byte[], byte[]> record = new ProducerRecord(this.topic, this.partition, timestamp, key, payload);
        Producer<byte[], byte[]> producer = this.lazyProducer.get();
        if (producer != null) {
            this.deliveryStrategy.send(this.lazyProducer.get(), record, e, this.failedDeliveryCallback);
        } else {
            this.failedDeliveryCallback.onFailedDelivery(e, (Throwable)null);
        }

    }

    protected Long getTimestamp(E e) {
        return e instanceof ILoggingEvent ? ((ILoggingEvent)e).getTimeStamp() : System.currentTimeMillis();
    }

    protected Producer<byte[], byte[]> createProducer() {
        return new KafkaProducer(new HashMap(this.producerConfig));
    }

    private void deferAppend(E event) {
        this.queue.add(event);
    }

    private void ensureDeferredAppends() {
        Object event;
        while((event = this.queue.poll()) != null) {
            if (this.initFailed) {
                this.failedDeliveryCallback.onFailedDelivery(event, (Throwable)null);
            } else {
                super.doAppend(event);
            }
        }

    }

    private class LazyProducer {
        private volatile Producer<byte[], byte[]> producer;

        private LazyProducer() {
        }

        public Producer<byte[], byte[]> get() {
            Producer<byte[], byte[]> result = this.producer;
            if (result == null) {
                synchronized(this) {
                    result = this.producer;
                    if (result == null) {
                        this.producer = result = this.initialize();
                    }
                }
            }

            return result;
        }

        protected Producer<byte[], byte[]> initialize() {
            Producer producer = null;

            try {
                KafkaAppender.this.initTime = System.currentTimeMillis();
                producer = KafkaAppender.this.createProducer();
            } catch (Exception var6) {
                KafkaAppender.this.addError("error creating producer", var6);
            } finally {
                KafkaAppender.this.initFailed = producer == null;
            }

            return producer;
        }

        public boolean isInitialized() {
            return this.producer != null;
        }
    }
}
