package com.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.net.SocketException;
import java.net.UnknownHostException;

public class IPLogConfig extends ClassicConverter {
    public IPLogConfig() {
    }

    public String convert(ILoggingEvent event) {
        try {
            return InetAddressHolder.getInetAddress().getHostAddress();
        } catch (SocketException | UnknownHostException var3) {
            var3.printStackTrace();
            return null;
        }
    }
}

