package com.gray;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "kucoin.gray"
)
public class GrayProperties {
    private GrayProperties.Custom custom = new GrayProperties.Custom();

    public GrayProperties() {
    }

    public GrayProperties.Custom getCustom() {
        return this.custom;
    }

    public void setCustom(GrayProperties.Custom custom) {
        this.custom = custom;
    }

    public static class Custom {
        private boolean enabled = false;
        private String requestKey = "X-ROUTE-KEY";
        private String serviceKey = "route";

        public Custom() {
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getRequestKey() {
            return this.requestKey;
        }

        public void setRequestKey(String requestKey) {
            this.requestKey = requestKey;
        }

        public String getServiceKey() {
            return this.serviceKey;
        }

        public void setServiceKey(String serviceKey) {
            this.serviceKey = serviceKey;
        }
    }
}

