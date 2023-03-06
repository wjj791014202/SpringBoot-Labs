package com.base.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {
    public DateTimeUtils() {
    }

    public static long systemNanoTime() {
        return DateTimeUtils.NanoTimeHelper.nanoTime();
    }

    public static String formatNanoTime(long nanoTime) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnnnnnZ").format(ZonedDateTime.ofInstant(Instant.ofEpochSecond(nanoTime / 1000000000L, nanoTime % 1000000000L), ZoneId.of("UTC")));
    }

    public static String formatNanoTime(long nanoTime, String zone) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnnnnnZ").format(ZonedDateTime.ofInstant(Instant.ofEpochSecond(nanoTime / 1000000000L, nanoTime % 1000000000L), ZoneId.of(zone)));
    }

    public static class NanoTimeHelper {
        private static final long initialTimes = System.currentTimeMillis();
        private static final long initialNanos = System.nanoTime();

        public NanoTimeHelper() {
        }

        public static long nanoTime() {
            long gapTime = System.nanoTime() - initialNanos;
            return gapTime > 0L ? TimeUnit.MILLISECONDS.toNanos(initialTimes) + gapTime : TimeUnit.MILLISECONDS.toNanos(initialTimes) - gapTime;
        }
    }
}

