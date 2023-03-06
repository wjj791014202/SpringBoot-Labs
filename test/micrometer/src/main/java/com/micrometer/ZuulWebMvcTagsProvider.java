package com.micrometer;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTags;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTagsProvider;

public class ZuulWebMvcTagsProvider implements WebMvcTagsProvider {
    public ZuulWebMvcTagsProvider() {
    }

    public Iterable<Tag> getTags(HttpServletRequest request, HttpServletResponse response, Object handler, Throwable exception) {
        return Tags.of(new Tag[]{WebMvcTags.method(request), this.getUri(request), WebMvcTags.exception(exception), WebMvcTags.status(response)});
    }

    public Iterable<Tag> getLongRequestTags(HttpServletRequest request, Object handler) {
        return Tags.of(new Tag[]{WebMvcTags.method(request), this.getUri(request)});
    }

    private Tag getUri(HttpServletRequest request) {
        return Tag.of("uri", request.getRequestURI());
    }
}

