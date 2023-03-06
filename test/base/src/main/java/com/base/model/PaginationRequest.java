package com.base.model;


import java.io.Serializable;

public class PaginationRequest implements Serializable {
    private static final long serialVersionUID = 8772692402144953760L;
    private Integer currentPage;
    private Integer pageSize;

    private static Integer $default$currentPage() {
        return 1;
    }

    private static Integer $default$pageSize() {
        return 50;
    }

    public static PaginationRequest.PaginationRequestBuilder builder() {
        return new PaginationRequest.PaginationRequestBuilder();
    }

    public Integer getCurrentPage() {
        return this.currentPage;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setCurrentPage(final Integer currentPage) {
        this.currentPage = currentPage;
    }

    public void setPageSize(final Integer pageSize) {
        this.pageSize = pageSize;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PaginationRequest)) {
            return false;
        } else {
            PaginationRequest other = (PaginationRequest)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$currentPage = this.getCurrentPage();
                Object other$currentPage = other.getCurrentPage();
                if (this$currentPage == null) {
                    if (other$currentPage != null) {
                        return false;
                    }
                } else if (!this$currentPage.equals(other$currentPage)) {
                    return false;
                }

                Object this$pageSize = this.getPageSize();
                Object other$pageSize = other.getPageSize();
                if (this$pageSize == null) {
                    if (other$pageSize != null) {
                        return false;
                    }
                } else if (!this$pageSize.equals(other$pageSize)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PaginationRequest;
    }

    public int hashCode() {
        int PRIME = true;
        int result = 1;
        Object $currentPage = this.getCurrentPage();
        int result = result * 59 + ($currentPage == null ? 43 : $currentPage.hashCode());
        Object $pageSize = this.getPageSize();
        result = result * 59 + ($pageSize == null ? 43 : $pageSize.hashCode());
        return result;
    }

    public String toString() {
        return "PaginationRequest(currentPage=" + this.getCurrentPage() + ", pageSize=" + this.getPageSize() + ")";
    }

    public PaginationRequest(final Integer currentPage, final Integer pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public PaginationRequest() {
        this.currentPage = $default$currentPage();
        this.pageSize = $default$pageSize();
    }

    public static class PaginationRequestBuilder {
        private boolean currentPage$set;
        private Integer currentPage;
        private boolean pageSize$set;
        private Integer pageSize;

        PaginationRequestBuilder() {
        }

        public PaginationRequest.PaginationRequestBuilder currentPage(final Integer currentPage) {
            this.currentPage = currentPage;
            this.currentPage$set = true;
            return this;
        }

        public PaginationRequest.PaginationRequestBuilder pageSize(final Integer pageSize) {
            this.pageSize = pageSize;
            this.pageSize$set = true;
            return this;
        }

        public PaginationRequest build() {
            Integer currentPage = this.currentPage;
            if (!this.currentPage$set) {
                currentPage = PaginationRequest.$default$currentPage();
            }

            Integer pageSize = this.pageSize;
            if (!this.pageSize$set) {
                pageSize = PaginationRequest.$default$pageSize();
            }

            return new PaginationRequest(currentPage, pageSize);
        }

        public String toString() {
            return "PaginationRequest.PaginationRequestBuilder(currentPage=" + this.currentPage + ", pageSize=" + this.pageSize + ")";
        }
    }
}

