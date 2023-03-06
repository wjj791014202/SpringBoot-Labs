package com.rediscache;


import java.io.Serializable;
import lombok.NonNull;

public class CacheMessage implements Serializable {
    @NonNull
    private String cacheName;
    @NonNull
    private Object key;
    @NonNull
    private boolean allEntries;
    private String id;

    @NonNull
    public String getCacheName() {
        return this.cacheName;
    }

    @NonNull
    public Object getKey() {
        return this.key;
    }

    @NonNull
    public boolean isAllEntries() {
        return this.allEntries;
    }

    public String getId() {
        return this.id;
    }

    public void setCacheName(@NonNull final String cacheName) {
        if (cacheName == null) {
            throw new NullPointerException("cacheName is marked @NonNull but is null");
        } else {
            this.cacheName = cacheName;
        }
    }

    public void setKey(@NonNull final Object key) {
        if (key == null) {
            throw new NullPointerException("key is marked @NonNull but is null");
        } else {
            this.key = key;
        }
    }

    public void setAllEntries(@NonNull final boolean allEntries) {
        this.allEntries = allEntries;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CacheMessage)) {
            return false;
        } else {
            CacheMessage other = (CacheMessage)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$cacheName = this.getCacheName();
                Object other$cacheName = other.getCacheName();
                if (this$cacheName == null) {
                    if (other$cacheName != null) {
                        return false;
                    }
                } else if (!this$cacheName.equals(other$cacheName)) {
                    return false;
                }

                Object this$key = this.getKey();
                Object other$key = other.getKey();
                if (this$key == null) {
                    if (other$key != null) {
                        return false;
                    }
                } else if (!this$key.equals(other$key)) {
                    return false;
                }

                if (this.isAllEntries() != other.isAllEntries()) {
                    return false;
                } else {
                    Object this$id = this.getId();
                    Object other$id = other.getId();
                    if (this$id == null) {
                        if (other$id != null) {
                            return false;
                        }
                    } else if (!this$id.equals(other$id)) {
                        return false;
                    }

                    return true;
                }
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof CacheMessage;
    }

    public int hashCode() {
        int PRIME = true;
        int result = 1;
        Object $cacheName = this.getCacheName();
        int result = result * 59 + ($cacheName == null ? 43 : $cacheName.hashCode());
        Object $key = this.getKey();
        result = result * 59 + ($key == null ? 43 : $key.hashCode());
        result = result * 59 + (this.isAllEntries() ? 79 : 97);
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        return result;
    }

    public String toString() {
        return "CacheMessage(cacheName=" + this.getCacheName() + ", key=" + this.getKey() + ", allEntries=" + this.isAllEntries() + ", id=" + this.getId() + ")";
    }

    public CacheMessage(@NonNull final String cacheName, @NonNull final Object key, @NonNull final boolean allEntries, final String id) {
        if (cacheName == null) {
            throw new NullPointerException("cacheName is marked @NonNull but is null");
        } else if (key == null) {
            throw new NullPointerException("key is marked @NonNull but is null");
        } else {
            this.cacheName = cacheName;
            this.key = key;
            this.allEntries = allEntries;
            this.id = id;
        }
    }

    public CacheMessage() {
    }

    public CacheMessage(@NonNull final String cacheName, @NonNull final Object key, @NonNull final boolean allEntries) {
        if (cacheName == null) {
            throw new NullPointerException("cacheName is marked @NonNull but is null");
        } else if (key == null) {
            throw new NullPointerException("key is marked @NonNull but is null");
        } else {
            this.cacheName = cacheName;
            this.key = key;
            this.allEntries = allEntries;
        }
    }
}

