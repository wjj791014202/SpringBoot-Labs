package com.kucoin.starter.chaincontext.parser;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public class DefaultRequestKeyParser implements IRequestKeyParser {
    public DefaultRequestKeyParser() {
    }

    public String parse(String key, HttpServletRequest request) {
        return (String) Optional.ofNullable(request.getHeader(key)).orElse(request.getParameter(key));
    }
}
