package com.log;


import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class HostNameLogConfig extends ClassicConverter {
    public HostNameLogConfig() {
    }

    public String convert(ILoggingEvent event) {
        return InetAddressHolder.getHostname();
    }
}
