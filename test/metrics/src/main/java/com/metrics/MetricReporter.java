package com.metrics;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Counter;
import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Reporter;
import com.codahale.metrics.Reservoir;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.StatsDClient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

@Slf4j
public class MetricReporter implements Reporter {

    private static final String[] EMPTY_TAGS = new String[]{};

    private final MetricRegistry registry;
    private final StatsDClient statsDClient;
    private final ScheduledExecutorService executor;
    private final Map<String, Long> lastCountMap = new ConcurrentHashMap<>();

    protected final List<Consumer<String[]>> subReportTaskList = new ArrayList<>();

    public MetricReporter(ScheduledExecutorService executor, MetricRegistry registry,
                          StatsDClient statsDClient, long outerInterval, TimeUnit timeUnit) {
        this.executor = executor;
        this.registry = registry;
        this.statsDClient = statsDClient == null ? new NoOpStatsDClient() : statsDClient;
        executor.scheduleWithFixedDelay(this::outerReport, 1000, outerInterval, timeUnit);
    }

    public void addSubReportTask(Consumer<String[]> consumer) {
        subReportTaskList.add(consumer);
    }

    public void outerReport() {
        try {
            String[] commonTags = getCommonTags();
            reportMetricRegistry(registry, commonTags);
            for (Consumer<String[]> consumer : this.subReportTaskList) {
                consumer.accept(commonTags);
            }
        } catch (Throwable throwable) {
            log.error("outerReport error", throwable);
        }
    }

    protected String[] getCommonTags() {
        return EMPTY_TAGS;
    }

    protected void reportMetricRegistry(MetricRegistry registry, String[] commonTags) {
        Map<String, Metric> metrics = registry.getMetrics();
        for (Map.Entry<String, Metric> entry : metrics.entrySet()) {
            if (Gauge.class.isInstance(entry.getValue())) {
                reportGauge(entry.getKey(), (Gauge<Number>) entry.getValue(), commonTags);
            } else if (Counter.class.isInstance(entry.getValue())) {
                reportCounter(entry.getKey(), (Counter) entry.getValue(), commonTags);
            } else if (Histogram.class.isInstance(entry.getValue())) {
                logHistogram(entry.getKey(), (Histogram) entry.getValue(), commonTags);
            } else if (Meter.class.isInstance(entry.getValue())) {
                reportMeter(entry.getKey(), (Meter) entry.getValue(), commonTags);
            } else if (Timer.class.isInstance(entry.getValue())) {
                logTimer(entry.getKey(), (Timer) entry.getValue(), commonTags);
            }
        }
    }

    @Override
    public void close() {
        executor.shutdown();
    }

    private void logTimer(String name, Timer timer, String[] commonTags) {
        Snapshot snapshot = timer.getSnapshot();
        Long lastCount = lastCountMap.get(name);
        long count = timer.getCount();
        if ((null == lastCount || lastCount != count) && count > 0) {
            statsSnapshot(name, snapshot, count, commonTags);
            resetTimer(timer);
            lastCountMap.put(name, count);
        }
    }

    private void logHistogram(String name, Histogram histogram, String[] commonTags) {
        Snapshot snapshot = histogram.getSnapshot();
        Long lastCount = lastCountMap.get(name);
        long count = histogram.getCount();
        if ((null == lastCount || lastCount != count) && count > 0) {
            statsSnapshot(name, snapshot, count, commonTags);
            resetHistogram(histogram);
            lastCountMap.put(name, count);
        }
    }

    private void statsSnapshot(String name, Snapshot snapshot, long count, String[] commonTags) {
        String key = rename(name);
        String[] tags = getTags(name, commonTags);
        statsDClient.gauge(key + "_total", count, tags);
        statsDClient.gauge(key + "_min", snapshot.getMin(), tags);
        statsDClient.gauge(key + "_max", snapshot.getMax(), tags);
        // statsDClient.gauge(key + "_std", snapshot.getStdDev(), tags);
        // statsDClient.gauge(key + "_median", snapshot.getMedian(), tags);
        statsDClient.gauge(key + "_p95", snapshot.get95thPercentile(), tags);
        statsDClient.gauge(key + "_p99", snapshot.get99thPercentile(), tags);
        statsDClient.gauge(key + "_p999", snapshot.get999thPercentile(), tags);
    }

    private void reportGauge(String name, Gauge<Number> gauge, String[] commonTags) {
        Number value = gauge.getValue();
        String key = rename(name);
        String[] tags = getTags(name, commonTags);

        if (value instanceof Integer) {
            statsDClient.gauge(key, value.intValue(), tags);
        } else if (value instanceof Long) {
            statsDClient.gauge(key, value.longValue(), tags);
        } else {
            statsDClient.gauge(key, value.doubleValue(), tags);
        }
    }

    private void reportCounter(String name, Counter counter, String[] commonTags) {
        String key = rename(name);
        String[] tags = getTags(name, commonTags);
        statsDClient.gauge(key, counter.getCount(), tags);
    }

    private void reportMeter(String name, Meter meter, String[] commonTags) {
        String key = rename(name);
        String[] tags = getTags(name, commonTags);
        statsDClient.gauge(key, meter.getCount(), tags);
    }

    protected String rename(String name) {
        return name;
    }

    protected String[] getTags(String name, String[] commonTags) {
        return commonTags;
    }

    private static final Field histogramField = Objects.requireNonNull(ReflectionUtils.findField(Timer.class, "histogram"), "Timer.histogram is null");
    private static final Field reservoirField = Objects.requireNonNull(ReflectionUtils.findField(Histogram.class, "reservoir"), "Histogram.reservoir is null");

    private static final Field valuesField = Objects.requireNonNull(ReflectionUtils.findField(ExponentiallyDecayingReservoir.class, "values"), "ExponentiallyDecayingReservoir.values is null");
    private static final Field lastScaleTickField = Objects.requireNonNull(ReflectionUtils.findField(ExponentiallyDecayingReservoir.class, "lastScaleTick"), "ExponentiallyDecayingReservoir.lastScaleTick is null");
    private static final Method rescaleHandle = Objects.requireNonNull(ReflectionUtils.findMethod(ExponentiallyDecayingReservoir.class, "rescale", long.class, long.class), "ExponentiallyDecayingReservoir.rescale() is null");
    private static final Method lockForRescaleHandle = Objects.requireNonNull(ReflectionUtils.findMethod(ExponentiallyDecayingReservoir.class, "lockForRescale"), "ExponentiallyDecayingReservoir.lockForRescale() is null");
    private static final Method unlockForRescaleHandle = Objects.requireNonNull(ReflectionUtils.findMethod(ExponentiallyDecayingReservoir.class, "unlockForRescale"), "ExponentiallyDecayingReservoir.unlockForRescale() is null");

    static {
        ReflectionUtils.makeAccessible(histogramField);
        ReflectionUtils.makeAccessible(reservoirField);
        ReflectionUtils.makeAccessible(valuesField);
        ReflectionUtils.makeAccessible(lastScaleTickField);
        ReflectionUtils.makeAccessible(lockForRescaleHandle);
        ReflectionUtils.makeAccessible(unlockForRescaleHandle);
        ReflectionUtils.makeAccessible(rescaleHandle);
    }

    private static void resetTimer(Timer timer) {
        Histogram histogram = (Histogram) ReflectionUtils.getField(histogramField, timer);
        resetHistogram(histogram);
    }

    private static void resetHistogram(Histogram histogram) {
        Clock clock = Clock.defaultClock();
        try {
            Reservoir reservoir = (Reservoir) ReflectionUtils.getField(reservoirField, histogram);
            if (!(reservoir instanceof ExponentiallyDecayingReservoir)) {
                log.error("type not match type={}", reservoir.getClass());
                return;
            }
            Map<?, ?> map = (Map<?, ?>) ReflectionUtils.getField(valuesField, reservoir);
            lockForRescaleHandle.invoke(reservoir);
            try {
                map.clear();
                AtomicLong lastScaleTick = (AtomicLong) ReflectionUtils.getField(lastScaleTickField, reservoir);
                rescaleHandle.invoke(reservoir, clock.getTick(), lastScaleTick.get());
            } finally {
                unlockForRescaleHandle.invoke(reservoir);
            }
        } catch (Exception e) {
            log.error("invoke", e);
        }
    }
}
