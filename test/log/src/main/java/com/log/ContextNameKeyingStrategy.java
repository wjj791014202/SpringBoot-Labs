package com.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.nio.ByteBuffer;

public class ContextNameKeyingStrategy extends ContextAwareBase implements KeyingStrategy<ILoggingEvent> {
    private byte[] contextNameHash = null;

    public ContextNameKeyingStrategy() {
    }

    public void setContext(Context context) {
        super.setContext(context);
        String hostname = context.getProperty("CONTEXT_NAME");
        if (hostname == null) {
            this.addError("Hostname could not be found in context. HostNamePartitioningStrategy will not work.");
        } else {
            this.contextNameHash = ByteBuffer.allocate(4).putInt(hostname.hashCode()).array();
        }

    }

    public byte[] createKey(ILoggingEvent e) {
        return this.contextNameHash;
    }
}
