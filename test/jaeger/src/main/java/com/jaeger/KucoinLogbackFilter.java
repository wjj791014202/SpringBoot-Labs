package com.jaeger;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import io.jaegertracing.internal.JaegerSpanContext;
import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;
import org.slf4j.MDC;
import org.slf4j.Marker;

public class KucoinLogbackFilter extends TurboFilter {
    public KucoinLogbackFilter() {
    }

    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (!level.isGreaterOrEqual(Level.INFO)) {
            return FilterReply.NEUTRAL;
        } else {
            if (GlobalTracer.isRegistered()) {
                Span span = GlobalTracer.get().activeSpan();
                if (span == null) {
                    return FilterReply.NEUTRAL;
                }

                JaegerSpanContext jaegerSpanContext = (JaegerSpanContext)GlobalTracer.get().activeSpan().context();
                String traceId = jaegerSpanContext.getTraceId();
                long spanId = jaegerSpanContext.getSpanId();
                MDC.put("X-TraceId", traceId);
                MDC.put("X-SpanId", Long.toHexString(spanId));
            }

            return FilterReply.NEUTRAL;
        }
    }
}

