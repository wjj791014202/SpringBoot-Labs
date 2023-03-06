package com.base.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class HasMore<E> implements Serializable {
    private static final long serialVersionUID = -4461365622721650594L;
    private List<E> dataList;
    private boolean hasMore;
    private Map<String, Object> context;

    public HasMore(List<E> dataList, boolean hasMore) {
        this.dataList = dataList;
        this.hasMore = hasMore;
    }

    public HasMore(List<E> dataList, HasMoreRequest hasMoreRequest) {
        int size = Math.min(dataList.size(), hasMoreRequest.getMaxCount());
        List<E> elemList = new ArrayList(size);

        for(int i = 0; i < size; ++i) {
            elemList.add(dataList.get(i));
        }

        if (hasMoreRequest.reverseBeforeReturn()) {
            Collections.reverse(elemList);
        }

        this.dataList = elemList;
        this.hasMore = dataList.size() > hasMoreRequest.getMaxCount();
    }

    public static <E> HasMore.HasMoreBuilder<E> builder() {
        return new HasMore.HasMoreBuilder();
    }

    public List<E> getDataList() {
        return this.dataList;
    }

    public boolean isHasMore() {
        return this.hasMore;
    }

    public Map<String, Object> getContext() {
        return this.context;
    }

    public void setDataList(final List<E> dataList) {
        this.dataList = dataList;
    }

    public void setHasMore(final boolean hasMore) {
        this.hasMore = hasMore;
    }

    public void setContext(final Map<String, Object> context) {
        this.context = context;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof HasMore)) {
            return false;
        } else {
            HasMore<?> other = (HasMore)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label39: {
                    Object this$dataList = this.getDataList();
                    Object other$dataList = other.getDataList();
                    if (this$dataList == null) {
                        if (other$dataList == null) {
                            break label39;
                        }
                    } else if (this$dataList.equals(other$dataList)) {
                        break label39;
                    }

                    return false;
                }

                if (this.isHasMore() != other.isHasMore()) {
                    return false;
                } else {
                    Object this$context = this.getContext();
                    Object other$context = other.getContext();
                    if (this$context == null) {
                        if (other$context != null) {
                            return false;
                        }
                    } else if (!this$context.equals(other$context)) {
                        return false;
                    }

                    return true;
                }
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof HasMore;
    }

    public int hashCode() {
        int PRIME = true;
        int result = 1;
        Object $dataList = this.getDataList();
        int result = result * 59 + ($dataList == null ? 43 : $dataList.hashCode());
        result = result * 59 + (this.isHasMore() ? 79 : 97);
        Object $context = this.getContext();
        result = result * 59 + ($context == null ? 43 : $context.hashCode());
        return result;
    }

    public String toString() {
        return "HasMore(dataList=" + this.getDataList() + ", hasMore=" + this.isHasMore() + ", context=" + this.getContext() + ")";
    }

    public HasMore(final List<E> dataList, final boolean hasMore, final Map<String, Object> context) {
        this.dataList = dataList;
        this.hasMore = hasMore;
        this.context = context;
    }

    public HasMore() {
    }

    public static class HasMoreBuilder<E> {
        private List<E> dataList;
        private boolean hasMore;
        private Map<String, Object> context;

        HasMoreBuilder() {
        }

        public HasMore.HasMoreBuilder<E> dataList(final List<E> dataList) {
            this.dataList = dataList;
            return this;
        }

        public HasMore.HasMoreBuilder<E> hasMore(final boolean hasMore) {
            this.hasMore = hasMore;
            return this;
        }

        public HasMore.HasMoreBuilder<E> context(final Map<String, Object> context) {
            this.context = context;
            return this;
        }

        public HasMore<E> build() {
            return new HasMore(this.dataList, this.hasMore, this.context);
        }

        public String toString() {
            return "HasMore.HasMoreBuilder(dataList=" + this.dataList + ", hasMore=" + this.hasMore + ", context=" + this.context + ")";
        }
    }
}
