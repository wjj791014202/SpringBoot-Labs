package com.swagger;

import java.util.HashMap;
import java.util.Map;

public class SwaggerConstants {
    public static final Map<String, String> CONFIG_MAP = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
            this.put("scan", "com.kucoin");
            this.put("title", "Kucoin Restful APIS");
            this.put("description", "Kucoin Restful APIS");
            this.put("contact.name", "kucoin");
            this.put("contact.url", "http://www.kucoin.com");
            this.put("contact.email", "admin@kucoin.com");
            this.put("version", "1.0");
        }
    };

    public SwaggerConstants() {
    }
}