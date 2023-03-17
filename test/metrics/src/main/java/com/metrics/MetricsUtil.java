package com.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.poloniex.starter.raft.util.DateUtil;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetricsUtil {

    public static final MetricRegistry DEFAULT_REGISTRY = new MetricRegistry();

    public static void latencyNano(String key, long start) {
        latency(DEFAULT_REGISTRY, key, DateUtil.nanoTime() - start, TimeUnit.NANOSECONDS);
    }

    public static void latencyMillis(String key, long start) {
        latency(DEFAULT_REGISTRY, key, DateUtil.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
    }

    public static void latency(final String key, final long duration, TimeUnit unit) {
        latency(DEFAULT_REGISTRY, key, duration, unit);
    }

    public static void times(String key, long times) {
        times(DEFAULT_REGISTRY, key, times);
    }

    public static void gauge(final String key, final long value) {
        gauge(DEFAULT_REGISTRY, key, value, CheckValue.NONE);
    }

    public static void size(final String key, final long value) {
        size(DEFAULT_REGISTRY, key, value);
    }

    public static void latency(MetricRegistry metricRegistry, final String key, final long duration, TimeUnit unit) {
        if (null == metricRegistry) {
            metricRegistry = DEFAULT_REGISTRY;
        }
        metricRegistry.timer(key).update(duration, unit);
    }

    /**
     * 统计次数
     *
     * @param key
     * @param times
     */
    public static void times(MetricRegistry metricRegistry, final String key, final long times) {
        if (null == metricRegistry) {
            metricRegistry = DEFAULT_REGISTRY;
        }
        metricRegistry.counter(key).inc(times);
    }

    private static final MetricRegistry.MetricSupplier<Gauge> LONG_GAUGE_SUPPLIER = () -> {
        return new LongGauge();
    };

    public static void gauge(MetricRegistry metricRegistry, final String key, final long value, CheckValue checkValue) {
        if (null == metricRegistry) {
            metricRegistry = DEFAULT_REGISTRY;
        }
        LongGauge gauge = (LongGauge) metricRegistry.gauge(key, LONG_GAUGE_SUPPLIER);
        switch (checkValue) {
            case SMALL_THAN_OLD:
                if (value >= gauge.value) {
                    log.error("key={}, old value={}, new value={}", key, gauge.value);
                }
                break;
            case BIGGER_THEN_OLD:
                if (value <= gauge.value) {
                    log.error("key={}, old value={}, new value={}", key, gauge.value);
                }
                break;
            default:
                break;
        }
        gauge.value = value;
    }

    public static void size(MetricRegistry metricRegistry, final String key, final long value) {
        if (null == metricRegistry) {
            metricRegistry = DEFAULT_REGISTRY;
        }
        metricRegistry.histogram(key).update(value);
    }


    static class LongGauge implements Gauge<Long> {

        private long value;

        @Override
        public Long getValue() {
            return value;
        }
    }

    public static enum CheckValue {
        NONE, BIGGER_THEN_OLD, SMALL_THAN_OLD;
    }
}
