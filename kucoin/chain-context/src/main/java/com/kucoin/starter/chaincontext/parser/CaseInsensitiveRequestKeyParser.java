package com.kucoin.starter.chaincontext.parser;


import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public class CaseInsensitiveRequestKeyParser extends DefaultRequestKeyParser {
    public CaseInsensitiveRequestKeyParser() {
    }

    public String parse(String key, HttpServletRequest request) {
        return (String) Optional.ofNullable(super.parse(key.toLowerCase(), request)).orElse(super.parse(key.toUpperCase(), request));
    }
}
