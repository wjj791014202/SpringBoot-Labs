package com.kucoin.starter.chaincontext.parser;

import com.kucoin.starter.chaincontext.utils.RequestUtils;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public class IpRequestKeyParser implements IRequestKeyParser {
    public IpRequestKeyParser() {
    }

    public String parse(String key, HttpServletRequest request) {
        String ip = (String) Optional.ofNullable(request.getHeader(key)).orElse(request.getParameter(key));
        return (String)Optional.ofNullable(ip).orElse(RequestUtils.getIpAddr(request));
    }
}
