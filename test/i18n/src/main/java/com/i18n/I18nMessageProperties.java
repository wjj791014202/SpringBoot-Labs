package com.i18n;

@ConfigurationProperties(
        prefix = "spring.messages"
)
public class I18nMessageProperties {
    private String basename = "classpath:i18n/messages";
    private long cacheMillis = -1L;
    private String encoding = "UTF-8";

    public I18nMessageProperties() {
    }

    public String getBasename() {
        return this.basename;
    }

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public long getCacheMillis() {
        return this.cacheMillis;
    }

    public void setCacheMillis(long cacheMillis) {
        this.cacheMillis = cacheMillis;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
