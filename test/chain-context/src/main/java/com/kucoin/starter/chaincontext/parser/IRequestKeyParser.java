package com.kucoin.starter.chaincontext.parser;

import javax.servlet.http.HttpServletRequest;

public interface IRequestKeyParser {
    String parse(String key, HttpServletRequest request);
}
