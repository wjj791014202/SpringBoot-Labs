package com.base.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

public class HasMoreRequest implements Serializable {
    private static final long serialVersionUID = -4461365622721650595L;
    private boolean reverse;
    private Object offset;
    private boolean forward;
    private int maxCount;

    public boolean lessThanOffset() {
        return this.isReverse() && this.isForward() || !this.isReverse() && !this.isForward();
    }

    public boolean greaterThanOffset() {
        return !this.lessThanOffset();
    }

    public boolean descOrder() {
        return this.getOffset() != null ? this.lessThanOffset() : this.isReverse();
    }

    public boolean reverseBeforeReturn() {
        return this.descOrder() != this.isReverse();
    }

    public <T> T getOffset() {
        return this.offset;
    }

    @JsonIgnore
    public int getLimit() {
        return this.getMaxCount() + 1;
    }

    private static boolean $default$reverse() {
        return true;
    }

    private static boolean $default$forward() {
        return true;
    }

    private static int $default$maxCount() {
        return 50;
    }

    public static HasMoreRequest.HasMoreRequestBuilder builder() {
        return new HasMoreRequest.HasMoreRequestBuilder();
    }

    public boolean isReverse() {
        return this.reverse;
    }

    public boolean isForward() {
        return this.forward;
    }

    public int getMaxCount() {
        return this.maxCount;
    }

    public void setReverse(final boolean reverse) {
        this.reverse = reverse;
    }

    public void setOffset(final Object offset) {
        this.offset = offset;
    }

    public void setForward(final boolean forward) {
        this.forward = forward;
    }

    public void setMaxCount(final int maxCount) {
        this.maxCount = maxCount;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof HasMoreRequest)) {
            return false;
        } else {
            HasMoreRequest other = (HasMoreRequest)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.isReverse() != other.isReverse()) {
                return false;
            } else {
                label33: {
                    Object this$offset = this.getOffset();
                    Object other$offset = other.getOffset();
                    if (this$offset == null) {
                        if (other$offset == null) {
                            break label33;
                        }
                    } else if (this$offset.equals(other$offset)) {
                        break label33;
                    }

                    return false;
                }

                if (this.isForward() != other.isForward()) {
                    return false;
                } else {
                    return this.getMaxCount() == other.getMaxCount();
                }
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof HasMoreRequest;
    }

    public int hashCode() {
        int PRIME = true;
        int result = 1;
        int result = result * 59 + (this.isReverse() ? 79 : 97);
        Object $offset = this.getOffset();
        result = result * 59 + ($offset == null ? 43 : $offset.hashCode());
        result = result * 59 + (this.isForward() ? 79 : 97);
        result = result * 59 + this.getMaxCount();
        return result;
    }

    public String toString() {
        return "HasMoreRequest(reverse=" + this.isReverse() + ", offset=" + this.getOffset() + ", forward=" + this.isForward() + ", maxCount=" + this.getMaxCount() + ")";
    }

    public HasMoreRequest(final boolean reverse, final Object offset, final boolean forward, final int maxCount) {
        this.reverse = reverse;
        this.offset = offset;
        this.forward = forward;
        this.maxCount = maxCount;
    }

    public HasMoreRequest() {
        this.reverse = $default$reverse();
        this.forward = $default$forward();
        this.maxCount = $default$maxCount();
    }

    public static class HasMoreRequestBuilder {
        private boolean reverse$set;
        private boolean reverse;
        private Object offset;
        private boolean forward$set;
        private boolean forward;
        private boolean maxCount$set;
        private int maxCount;

        HasMoreRequestBuilder() {
        }

        public HasMoreRequest.HasMoreRequestBuilder reverse(final boolean reverse) {
            this.reverse = reverse;
            this.reverse$set = true;
            return this;
        }

        public HasMoreRequest.HasMoreRequestBuilder offset(final Object offset) {
            this.offset = offset;
            return this;
        }

        public HasMoreRequest.HasMoreRequestBuilder forward(final boolean forward) {
            this.forward = forward;
            this.forward$set = true;
            return this;
        }

        public HasMoreRequest.HasMoreRequestBuilder maxCount(final int maxCount) {
            this.maxCount = maxCount;
            this.maxCount$set = true;
            return this;
        }

        public HasMoreRequest build() {
            boolean reverse = this.reverse;
            if (!this.reverse$set) {
                reverse = HasMoreRequest.$default$reverse();
            }

            boolean forward = this.forward;
            if (!this.forward$set) {
                forward = HasMoreRequest.$default$forward();
            }

            int maxCount = this.maxCount;
            if (!this.maxCount$set) {
                maxCount = HasMoreRequest.$default$maxCount();
            }

            return new HasMoreRequest(reverse, this.offset, forward, maxCount);
        }

        public String toString() {
            return "HasMoreRequest.HasMoreRequestBuilder(reverse=" + this.reverse + ", offset=" + this.offset + ", forward=" + this.forward + ", maxCount=" + this.maxCount + ")";
        }
    }
}
