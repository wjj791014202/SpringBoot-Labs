package com.rediscache;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kucoin.cache")
public class KucoinCacheProperties {
    private KucoinCacheProperties.SerializerType serializer = new KucoinCacheProperties.SerializerType();
    private KucoinCacheProperties.Lettuce lettuce = new KucoinCacheProperties.Lettuce();
    private KucoinCacheProperties.Notify notify = new KucoinCacheProperties.Notify();
    private KucoinCacheProperties.TwoLevel twoLevel = new KucoinCacheProperties.TwoLevel();

    public KucoinCacheProperties() {
    }

    public KucoinCacheProperties.SerializerType getSerializer() {
        return this.serializer;
    }

    public KucoinCacheProperties.Lettuce getLettuce() {
        return this.lettuce;
    }

    public KucoinCacheProperties.Notify getNotify() {
        return this.notify;
    }

    public KucoinCacheProperties.TwoLevel getTwoLevel() {
        return this.twoLevel;
    }

    public void setSerializer(final KucoinCacheProperties.SerializerType serializer) {
        this.serializer = serializer;
    }

    public void setLettuce(final KucoinCacheProperties.Lettuce lettuce) {
        this.lettuce = lettuce;
    }

    public void setNotify(final KucoinCacheProperties.Notify notify) {
        this.notify = notify;
    }

    public void setTwoLevel(final KucoinCacheProperties.TwoLevel twoLevel) {
        this.twoLevel = twoLevel;
    }

    static class Lettuce {
        private long periodicRefreshInSeconds = 10L;
        private long refreshTriggersTimeoutInSeconds = 120L;

        public Lettuce() {
        }

        public long getPeriodicRefreshInSeconds() {
            return this.periodicRefreshInSeconds;
        }

        public long getRefreshTriggersTimeoutInSeconds() {
            return this.refreshTriggersTimeoutInSeconds;
        }

        public void setPeriodicRefreshInSeconds(final long periodicRefreshInSeconds) {
            this.periodicRefreshInSeconds = periodicRefreshInSeconds;
        }

        public void setRefreshTriggersTimeoutInSeconds(final long refreshTriggersTimeoutInSeconds) {
            this.refreshTriggersTimeoutInSeconds = refreshTriggersTimeoutInSeconds;
        }

        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof KucoinCacheProperties.Lettuce)) {
                return false;
            } else {
                KucoinCacheProperties.Lettuce other = (KucoinCacheProperties.Lettuce)o;
                if (!other.canEqual(this)) {
                    return false;
                } else if (this.getPeriodicRefreshInSeconds() != other.getPeriodicRefreshInSeconds()) {
                    return false;
                } else {
                    return this.getRefreshTriggersTimeoutInSeconds() == other.getRefreshTriggersTimeoutInSeconds();
                }
            }
        }

        protected boolean canEqual(final Object other) {
            return other instanceof KucoinCacheProperties.Lettuce;
        }

        public int hashCode() {
            int PRIME = true;
            int result = 1;
            long $periodicRefreshInSeconds = this.getPeriodicRefreshInSeconds();
            int result = result * 59 + (int)($periodicRefreshInSeconds >>> 32 ^ $periodicRefreshInSeconds);
            long $refreshTriggersTimeoutInSeconds = this.getRefreshTriggersTimeoutInSeconds();
            result = result * 59 + (int)($refreshTriggersTimeoutInSeconds >>> 32 ^ $refreshTriggersTimeoutInSeconds);
            return result;
        }

        public String toString() {
            return "KucoinCacheProperties.Lettuce(periodicRefreshInSeconds=" + this.getPeriodicRefreshInSeconds() + ", refreshTriggersTimeoutInSeconds=" + this.getRefreshTriggersTimeoutInSeconds() + ")";
        }
    }

    public static class SerializerType {
        public static final String VALUE_FASTJSON = "fastjson";
        public static final String VALUE_JACKSON = "jackson";
        public static final String VALUE_KRYO = "kryo";
        public static final String VALUE_GZIP_FASTJSON = "gzip_fastjson";
        private String value = "fastjson";

        public SerializerType() {
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(final String value) {
            this.value = value;
        }

        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof KucoinCacheProperties.SerializerType)) {
                return false;
            } else {
                KucoinCacheProperties.SerializerType other = (KucoinCacheProperties.SerializerType)o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    Object this$value = this.getValue();
                    Object other$value = other.getValue();
                    if (this$value == null) {
                        if (other$value != null) {
                            return false;
                        }
                    } else if (!this$value.equals(other$value)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(final Object other) {
            return other instanceof KucoinCacheProperties.SerializerType;
        }

        public int hashCode() {
            int PRIME = true;
            int result = 1;
            Object $value = this.getValue();
            int result = result * 59 + ($value == null ? 43 : $value.hashCode());
            return result;
        }

        public String toString() {
            return "KucoinCacheProperties.SerializerType(value=" + this.getValue() + ")";
        }
    }

    static class TwoLevel {
        private boolean enabled = false;

        TwoLevel() {
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }
    }

    static class Notify {
        public static final String TYPE_CHANNEL = "CHANNEL";
        public static final String TYPE_PATTERN = "PATTERN";
        private String topic;
        private String type = "CHANNEL";

        Notify() {
        }

        public String getTopic() {
            return this.topic;
        }

        public String getType() {
            return this.type;
        }

        public void setTopic(final String topic) {
            this.topic = topic;
        }

        public void setType(final String type) {
            this.type = type;
        }
    }
}
