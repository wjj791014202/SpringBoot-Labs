package com.log;


import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.spi.AppenderAttachable;
import com.kucoin.starter.log.appender.delivery.AsynchronousDeliveryStrategy;
import com.kucoin.starter.log.appender.delivery.DeliveryStrategy;
import com.kucoin.starter.log.appender.keying.KeyingStrategy;
import com.kucoin.starter.log.appender.keying.NoKeyKeyingStrategy;
import java.util.HashMap;
import java.util.Map;

public abstract class KafkaAppenderConfig<E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<E> {
    protected String topic = null;
    protected Encoder<E> encoder = null;
    protected KeyingStrategy<? super E> keyingStrategy = null;
    protected DeliveryStrategy deliveryStrategy;
    protected Integer partition = null;
    protected boolean appendTimestamp = true;
    protected Map<String, Object> producerConfig = new HashMap();

    public KafkaAppenderConfig() {
    }

    protected boolean checkPrerequisites() {
        boolean errorFree = true;
        if (this.producerConfig.get("bootstrap.servers") == null) {
            this.addError("No \"bootstrap.servers\" set for the appender named [\"" + this.name + "\"].");
            errorFree = false;
        }

        if (this.topic == null) {
            this.addError("No topic set for the appender named [\"" + this.name + "\"].");
            errorFree = false;
        }

        if (this.encoder == null) {
            this.addError("No encoder set for the appender named [\"" + this.name + "\"].");
            errorFree = false;
        }

        if (this.keyingStrategy == null) {
            this.addInfo("No explicit keyingStrategy set for the appender named [\"" + this.name + "\"]. Using default NoKeyKeyingStrategy.");
            this.keyingStrategy = new NoKeyKeyingStrategy();
        }

        if (this.deliveryStrategy == null) {
            this.addInfo("No explicit deliveryStrategy set for the appender named [\"" + this.name + "\"]. Using default asynchronous strategy.");
            this.deliveryStrategy = new AsynchronousDeliveryStrategy();
        }

        return errorFree;
    }

    public void setEncoder(Encoder<E> encoder) {
        this.encoder = encoder;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setKeyingStrategy(KeyingStrategy<? super E> keyingStrategy) {
        this.keyingStrategy = keyingStrategy;
    }

    public void addProducerConfig(String keyValue) {
        String[] split = keyValue.split("=", 2);
        if (split.length == 2) {
            this.addProducerConfigValue(split[0], split[1]);
        }

    }

    public void addProducerConfigValue(String key, Object value) {
        this.producerConfig.put(key, value);
    }

    public Map<String, Object> getProducerConfig() {
        return this.producerConfig;
    }

    public void setDeliveryStrategy(DeliveryStrategy deliveryStrategy) {
        this.deliveryStrategy = deliveryStrategy;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public boolean isAppendTimestamp() {
        return this.appendTimestamp;
    }

    public void setAppendTimestamp(boolean appendTimestamp) {
        this.appendTimestamp = appendTimestamp;
    }

    public void addProducerConfigs(String configs) {
        if (configs != null && configs.length() != 0) {
            String[] pconfigs = configs.split(",");
            String[] var3 = pconfigs;
            int var4 = pconfigs.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String s = var3[var5];
                this.addProducerConfig(s);
            }

        }
    }
}

