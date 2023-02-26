package com.kucoin.starter.chaincontext.autoconfig;

import com.kucoin.starter.chaincontext.parser.CaseInsensitiveRequestKeyParser;
import com.kucoin.starter.chaincontext.parser.DefaultRequestKeyParser;
import com.kucoin.starter.chaincontext.parser.IRequestKeyParser;
import com.kucoin.starter.chaincontext.parser.IpRequestKeyParser;
import java.util.HashMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChainContextPropertiesWrapper extends HashMap<String, IRequestKeyParser> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ChainContextPropertiesWrapper.class);
    private ChainContextProperties contextProperties;

    public ChainContextPropertiesWrapper(ChainContextProperties contextProperties) {
        this.contextProperties = contextProperties;
    }

    public void initWrapper() {
        this.addDefaultParser();
        if (this.contextProperties != null && CollectionUtils.isNotEmpty(this.contextProperties.getKeys())) {
            this.contextProperties.getKeys().forEach((keysParser) -> {
                if (StringUtils.isNotEmpty(keysParser.getParser())) {
                    try {
                        IRequestKeyParser parser = (IRequestKeyParser) ClassUtils.getClass(keysParser.getParser()).newInstance();
                        if (parser != null) {
                            this.put(keysParser.getKey(), parser);
                        }
                    } catch (Exception var3) {
                        LOG.error("fail to convert class:{} to IRequestKeyParser", keysParser.getParser(), var3);
                    }
                } else {
                    LOG.warn("fail to get key: {} while parser is empty", keysParser.getKey());
                }

            });
        }

    }

    private void addDefaultParser() {
        IRequestKeyParser defaultRequestKeyParser = new DefaultRequestKeyParser();
        this.put("X-DOMAIN", defaultRequestKeyParser);
        this.put("X-DOMAIN-ID", defaultRequestKeyParser);
        this.put("X-USER-ID", defaultRequestKeyParser);
        this.put("X-SESSION-ID", defaultRequestKeyParser);
        this.put("X-DEVICE-ID", defaultRequestKeyParser);
        this.put("X-COUNTRY", defaultRequestKeyParser);
        this.put("LANG", defaultRequestKeyParser);
        this.put("X-VERSION", new CaseInsensitiveRequestKeyParser());
        this.put("X-IP", new IpRequestKeyParser());
    }
}

