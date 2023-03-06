package com.base.model;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ObjectId implements Comparable<ObjectId>, Serializable {
    private static final long serialVersionUID = 3670079982654483072L;
    static final Logger LOGGER = LoggerFactory.getLogger("ObjectId");
    private static final int LOW_ORDER_THREE_BYTES = 16777215;
    private static final int MACHINE_IDENTIFIER;
    private static final short PROCESS_IDENTIFIER;
    private static final AtomicInteger NEXT_COUNTER = new AtomicInteger((new SecureRandom()).nextInt());
    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private final int timestamp;
    private final int machineIdentifier;
    private final short processIdentifier;
    private final int counter;

    public static ObjectId get() {
        return new ObjectId();
    }

    public static String getString() {
        return (new ObjectId()).toString();
    }

    public static boolean isValid(final String hexString) {
        if (hexString == null) {
            throw new IllegalArgumentException();
        } else {
            int len = hexString.length();
            if (len != 24) {
                return false;
            } else {
                for(int i = 0; i < len; ++i) {
                    char c = hexString.charAt(i);
                    if ((c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F')) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public static int getGeneratedMachineIdentifier() {
        return MACHINE_IDENTIFIER;
    }

    public static int getGeneratedProcessIdentifier() {
        return PROCESS_IDENTIFIER;
    }

    public static int getCurrentCounter() {
        return NEXT_COUNTER.get();
    }

    public static ObjectId createFromLegacyFormat(final int time, final int machine, final int inc) {
        return new ObjectId(time, machine, inc);
    }

    public ObjectId() {
        this(new Date());
    }

    public ObjectId(final Date date) {
        this(dateToTimestampSeconds(date), MACHINE_IDENTIFIER, PROCESS_IDENTIFIER, NEXT_COUNTER.getAndIncrement(), false);
    }

    public ObjectId(final Date date, final int counter) {
        this(date, MACHINE_IDENTIFIER, PROCESS_IDENTIFIER, counter);
    }

    public ObjectId(final Date date, final int machineIdentifier, final short processIdentifier, final int counter) {
        this(dateToTimestampSeconds(date), machineIdentifier, processIdentifier, counter);
    }

    public ObjectId(final int timestamp, final int machineIdentifier, final short processIdentifier, final int counter) {
        this(timestamp, machineIdentifier, processIdentifier, counter, true);
    }

    private ObjectId(final int timestamp, final int machineIdentifier, final short processIdentifier, final int counter, final boolean checkCounter) {
        if ((machineIdentifier & -16777216) != 0) {
            throw new IllegalArgumentException("The machine identifier must be between 0 and 16777215 (it must fit in three bytes).");
        } else if (checkCounter && (counter & -16777216) != 0) {
            throw new IllegalArgumentException("The counter must be between 0 and 16777215 (it must fit in three bytes).");
        } else {
            this.timestamp = timestamp;
            this.machineIdentifier = machineIdentifier;
            this.processIdentifier = processIdentifier;
            this.counter = counter & 16777215;
        }
    }

    public ObjectId(final String hexString) {
        this(parseHexString(hexString));
    }

    public ObjectId(final byte[] bytes) {
        this(ByteBuffer.wrap((byte[])notNull("bytes", bytes)));
    }

    ObjectId(final int timestamp, final int machineAndProcessIdentifier, final int counter) {
        this(legacyToBytes(timestamp, machineAndProcessIdentifier, counter));
    }

    public ObjectId(final ByteBuffer buffer) {
        notNull("buffer", buffer);
        isTrueArgument("buffer.remaining() >=12", buffer.remaining() >= 12);
        this.timestamp = makeInt(buffer.get(), buffer.get(), buffer.get(), buffer.get());
        this.machineIdentifier = makeInt((byte)0, buffer.get(), buffer.get(), buffer.get());
        this.processIdentifier = (short)makeInt((byte)0, (byte)0, buffer.get(), buffer.get());
        this.counter = makeInt((byte)0, buffer.get(), buffer.get(), buffer.get());
    }

    private static byte[] legacyToBytes(final int timestamp, final int machineAndProcessIdentifier, final int counter) {
        byte[] bytes = new byte[]{int3(timestamp), int2(timestamp), int1(timestamp), int0(timestamp), int3(machineAndProcessIdentifier), int2(machineAndProcessIdentifier), int1(machineAndProcessIdentifier), int0(machineAndProcessIdentifier), int3(counter), int2(counter), int1(counter), int0(counter)};
        return bytes;
    }

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        this.putToByteBuffer(buffer);
        return buffer.array();
    }

    public void putToByteBuffer(final ByteBuffer buffer) {
        notNull("buffer", buffer);
        isTrueArgument("buffer.remaining() >=12", buffer.remaining() >= 12);
        buffer.put(int3(this.timestamp));
        buffer.put(int2(this.timestamp));
        buffer.put(int1(this.timestamp));
        buffer.put(int0(this.timestamp));
        buffer.put(int2(this.machineIdentifier));
        buffer.put(int1(this.machineIdentifier));
        buffer.put(int0(this.machineIdentifier));
        buffer.put(short1(this.processIdentifier));
        buffer.put(short0(this.processIdentifier));
        buffer.put(int2(this.counter));
        buffer.put(int1(this.counter));
        buffer.put(int0(this.counter));
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public int getMachineIdentifier() {
        return this.machineIdentifier;
    }

    public short getProcessIdentifier() {
        return this.processIdentifier;
    }

    public int getCounter() {
        return this.counter;
    }

    public Date getDate() {
        return new Date((long)this.timestamp * 1000L);
    }

    public String toHexString() {
        char[] chars = new char[24];
        int i = 0;
        byte[] var3 = this.toByteArray();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            byte b = var3[var5];
            chars[i++] = HEX_CHARS[b >> 4 & 15];
            chars[i++] = HEX_CHARS[b & 15];
        }

        return new String(chars);
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ObjectId objectId = (ObjectId)o;
            if (this.counter != objectId.counter) {
                return false;
            } else if (this.machineIdentifier != objectId.machineIdentifier) {
                return false;
            } else if (this.processIdentifier != objectId.processIdentifier) {
                return false;
            } else {
                return this.timestamp == objectId.timestamp;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.timestamp;
        result = 31 * result + this.machineIdentifier;
        result = 31 * result + this.processIdentifier;
        result = 31 * result + this.counter;
        return result;
    }

    public int compareTo(final ObjectId other) {
        if (other == null) {
            throw new NullPointerException();
        } else {
            byte[] byteArray = this.toByteArray();
            byte[] otherByteArray = other.toByteArray();

            for(int i = 0; i < 12; ++i) {
                if (byteArray[i] != otherByteArray[i]) {
                    return (byteArray[i] & 255) < (otherByteArray[i] & 255) ? -1 : 1;
                }
            }

            return 0;
        }
    }

    public String toString() {
        return this.toHexString();
    }

    /** @deprecated */
    @Deprecated
    public int getTimeSecond() {
        return this.timestamp;
    }

    /** @deprecated */
    @Deprecated
    public long getTime() {
        return (long)this.timestamp * 1000L;
    }

    /** @deprecated */
    @Deprecated
    public String toStringMongod() {
        return this.toHexString();
    }

    private static int createMachineIdentifier() {
        int machinePiece;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration e = NetworkInterface.getNetworkInterfaces();

            while(e.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface)e.nextElement();
                sb.append(ni.toString());
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    ByteBuffer bb = ByteBuffer.wrap(mac);

                    try {
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                    } catch (BufferUnderflowException var7) {
                    }
                }
            }

            machinePiece = sb.toString().hashCode();
        } catch (Throwable var8) {
            machinePiece = (new SecureRandom()).nextInt();
            LOGGER.warn("Failed to get machine identifier from network interface, using random number instead", var8);
        }

        machinePiece &= 16777215;
        return machinePiece;
    }

    private static short createProcessIdentifier() {
        short processId;
        try {
            String processName = ManagementFactory.getRuntimeMXBean().getName();
            if (processName.contains("@")) {
                processId = (short)Integer.parseInt(processName.substring(0, processName.indexOf(64)));
            } else {
                processId = (short)ManagementFactory.getRuntimeMXBean().getName().hashCode();
            }
        } catch (Throwable var2) {
            processId = (short)(new SecureRandom()).nextInt();
            LOGGER.warn("Failed to get process identifier from JMX, using random number instead", var2);
        }

        return processId;
    }

    private static byte[] parseHexString(final String s) {
        if (!isValid(s)) {
            throw new IllegalArgumentException("invalid hexadecimal representation of an ObjectId: [" + s + "]");
        } else {
            byte[] b = new byte[12];

            for(int i = 0; i < b.length; ++i) {
                b[i] = (byte)Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
            }

            return b;
        }
    }

    private static int dateToTimestampSeconds(final Date time) {
        return (int)(time.getTime() / 1000L);
    }

    private static int makeInt(final byte b3, final byte b2, final byte b1, final byte b0) {
        return b3 << 24 | (b2 & 255) << 16 | (b1 & 255) << 8 | b0 & 255;
    }

    private static byte int3(final int x) {
        return (byte)(x >> 24);
    }

    private static byte int2(final int x) {
        return (byte)(x >> 16);
    }

    private static byte int1(final int x) {
        return (byte)(x >> 8);
    }

    private static byte int0(final int x) {
        return (byte)x;
    }

    private static byte short1(final short x) {
        return (byte)(x >> 8);
    }

    private static byte short0(final short x) {
        return (byte)x;
    }

    private static <T> T notNull(String name, T value) {
        if (value == null) {
            throw new IllegalArgumentException(name + " can not be null");
        } else {
            return value;
        }
    }

    private static void isTrueArgument(String name, boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException("state should be: " + name);
        }
    }

    static {
        try {
            MACHINE_IDENTIFIER = createMachineIdentifier();
            PROCESS_IDENTIFIER = createProcessIdentifier();
        } catch (Exception var1) {
            throw new RuntimeException(var1);
        }
    }
}

