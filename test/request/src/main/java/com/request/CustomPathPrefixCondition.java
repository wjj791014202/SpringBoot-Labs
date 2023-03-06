package com.request;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

public class CustomPathPrefixCondition implements RequestCondition<CustomPathPrefixCondition> {
    private String prefixPath;

    public CustomPathPrefixCondition(String prefixPath) {
        this.prefixPath = prefixPath;
    }

    public CustomPathPrefixCondition combine(CustomPathPrefixCondition other) {
        return new CustomPathPrefixCondition(other.getPrefixPath());
    }

    public CustomPathPrefixCondition getMatchingCondition(HttpServletRequest request) {
        return this.matchPrefixPath(request) ? this : null;
    }

    public int compareTo(CustomPathPrefixCondition other, HttpServletRequest request) {
        return other.getPrefixPath() == null ? -1 : (this.prefixPath == null ? 1 : other.getPrefixPath().compareTo(this.prefixPath));
    }

    public String getPrefixPath() {
        return this.prefixPath;
    }

    private boolean matchPrefixPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (StringUtils.isNotBlank(contextPath)) {
            uri = uri.substring(contextPath.length());
        }

        return uri.startsWith(this.prefixPath);
    }
}
