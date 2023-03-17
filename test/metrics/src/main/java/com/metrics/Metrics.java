package com.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Collect metrics.
 *
 * @author liaoxuefeng
 */
@Component
public final class Metrics {

	static class MetricsLogger {
		static final Logger logger = LoggerFactory.getLogger(MetricsLogger.class);
	}

	final Logger logger = LoggerFactory.getLogger(getClass());

	static MetricRegistry registry;
	static ScheduledReporter reporter;

	@PostConstruct
	public void init() {
		MetricsLogger.logger.info("init metric registry...");
		registry = new MetricRegistry();
		MetricsLogger.logger.info("init metric Slf4jReporter...");
		reporter = Slf4jReporter.forRegistry(registry).outputTo(logger).convertRatesTo(TimeUnit.SECONDS)
				.convertDurationsTo(TimeUnit.MILLISECONDS).build();
		reporter.start(1, TimeUnit.MINUTES);
	}

	static Map<String, Timer> timers = new ConcurrentHashMap<>();

	public static TimerMetric measureTime(String name) {

		if (reporter == null) {
			return FALLBACK;
		}
		Timer timer = timers.get(name);
		if (timer == null) {
			timer = registry.timer(name);
			timers.put(name, timer);
		}
		return new TimerMetricsImpl(timer.time());
	}

	static final TimerMetric FALLBACK = new TimerMetricFallback();
}

class TimerMetricFallback implements TimerMetric {
	@Override
	public void close() {
	}
}

class TimerMetricsImpl implements TimerMetric {

	final Timer.Context context;

	TimerMetricsImpl(Timer.Context context) {
		this.context = context;
	}

	@Override
	public void close() {
		this.context.stop();
	}

}
