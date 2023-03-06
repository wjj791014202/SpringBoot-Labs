package com.log;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import java.nio.ByteBuffer;

public class HostNameKeyingStrategy extends ContextAwareBase implements KeyingStrategy<Object>, LifeCycle {
    private byte[] hostnameHash = null;
    private boolean errorWasShown = false;

    public HostNameKeyingStrategy() {
    }

    public void setContext(Context context) {
        super.setContext(context);
        String hostname = context.getProperty("HOSTNAME");
        if (hostname == null) {
            if (!this.errorWasShown) {
                this.addError("Hostname could not be found in context. HostNamePartitioningStrategy will not work.");
                this.errorWasShown = true;
            }
        } else {
            this.hostnameHash = ByteBuffer.allocate(4).putInt(hostname.hashCode()).array();
        }

    }

    public byte[] createKey(Object e) {
        return this.hostnameHash;
    }

    public void start() {
    }

    public void stop() {
        this.errorWasShown = false;
    }

    public boolean isStarted() {
        return true;
    }
}
