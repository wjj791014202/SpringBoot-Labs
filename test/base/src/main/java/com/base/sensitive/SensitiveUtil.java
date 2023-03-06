package com.base.sensitive;


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;

public class SensitiveUtil {
    private static final String MASK = "*";
    private static final String EQUALS = "=";
    private static final String COMMA = ",";
    private static final List<String> MASK_KEY_SUFFIX_LIST = Arrays.asList("key", "password", "code");

    public SensitiveUtil() {
    }

    public static String toString(Map<String, ?> paramMap) {
        if (paramMap == null) {
            return null;
        } else {
            StringBuilder valueBuilder = new StringBuilder();
            Iterator var2 = paramMap.entrySet().iterator();

            while(var2.hasNext()) {
                Entry<String, ?> entry = (Entry)var2.next();
                valueBuilder.append(",").append((String)entry.getKey()).append("=").append(toString((String)entry.getKey(), entry.getValue()));
            }

            return valueBuilder.length() > 0 && valueBuilder.charAt(0) == ",".charAt(0) ? valueBuilder.substring(1) : valueBuilder.toString();
        }
    }

    public static String toString(String key, Object value) {
        if (value == null) {
            return null;
        } else if (!needMask(key)) {
            return value.toString();
        } else {
            String stringValue = value.toString();
            if (stringValue.equals("")) {
                return stringValue;
            } else if (stringValue.length() == 1) {
                return "*";
            } else {
                StringBuilder valueBuilder = new StringBuilder();
                if (stringValue.length() == 2) {
                    valueBuilder.append("*").append("*");
                    return valueBuilder.toString();
                } else {
                    for(int i = 0; i < stringValue.length(); ++i) {
                        if (i != 0 && i != stringValue.length() - 1) {
                            valueBuilder.append("*");
                        } else {
                            valueBuilder.append(stringValue.charAt(i));
                        }
                    }

                    return valueBuilder.toString();
                }
            }
        }
    }

    private static boolean needMask(String key) {
        Iterator var1 = MASK_KEY_SUFFIX_LIST.iterator();

        String maskSuffix;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            maskSuffix = (String)var1.next();
        } while(!StringUtils.endsWithIgnoreCase(key, maskSuffix));

        return true;
    }
}

