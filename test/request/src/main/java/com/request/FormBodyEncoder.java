package com.request;

import com.google.common.collect.Maps;
import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.codec.Encoder.Default;
import feign.form.ContentProcessor;
import feign.form.ContentType;
import feign.form.MultipartFormContentProcessor;
import feign.form.UrlencodedFormContentProcessor;
import feign.form.util.CharsetUtil;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormBodyEncoder implements Encoder {
    private static final Logger LOG = LoggerFactory.getLogger(FormBodyEncoder.class);
    private static final String FORM_PARAM_PREFIX = "FORM_PARAM_PREFIX_";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final Pattern CHARSET_PATTERN = Pattern.compile("(?<=charset=)([\\w\\-]+)");
    Encoder delegate;
    Map<ContentType, ContentProcessor> processors;

    public FormBodyEncoder() {
        this(new Default());
    }

    public FormBodyEncoder(Encoder delegate) {
        this.delegate = delegate;
        List<ContentProcessor> list = Arrays.asList(new MultipartFormContentProcessor(delegate), new UrlencodedFormContentProcessor());
        this.processors = new HashMap(list.size(), 1.0F);
        Iterator var3 = list.iterator();

        while(var3.hasNext()) {
            ContentProcessor processor = (ContentProcessor)var3.next();
            this.processors.put(processor.getSupportedContentType(), processor);
        }

    }

    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        String contentTypeValue = this.getContentTypeValue(template.headers());
        ContentType contentType = ContentType.of(contentTypeValue);
        if (MAP_STRING_WILDCARD.equals(bodyType) && this.processors.containsKey(contentType)) {
            Charset charset = this.getCharset(contentTypeValue);
            Map<String, Object> data = (Map)object;
            Map<String, Object> realData = Maps.newHashMap();
            data.forEach((key, value) -> {
                if (key.startsWith("FORM_PARAM_PREFIX_")) {
                    realData.putAll(beanToMap(value));
                } else if (value != null) {
                    realData.put(key, value);
                }

            });

            try {
                ((ContentProcessor)this.processors.get(contentType)).process(template, charset, realData);
            } catch (Exception var10) {
                throw new EncodeException(var10.getMessage());
            }
        } else {
            this.delegate.encode(object, bodyType, template);
        }
    }

    public final ContentProcessor getContentProcessor(ContentType type) {
        return (ContentProcessor)this.processors.get(type);
    }

    private String getContentTypeValue(Map<String, Collection<String>> headers) {
        Iterator var2 = headers.entrySet().iterator();

        while(true) {
            Entry entry;
            do {
                if (!var2.hasNext()) {
                    return null;
                }

                entry = (Entry)var2.next();
            } while(!((String)entry.getKey()).equalsIgnoreCase(CONTENT_TYPE_HEADER));

            Iterator var4 = ((Collection)entry.getValue()).iterator();

            while(var4.hasNext()) {
                String contentTypeValue = (String)var4.next();
                if (contentTypeValue != null) {
                    return contentTypeValue;
                }
            }
        }
    }

    private Charset getCharset(String contentTypeValue) {
        Matcher matcher = CHARSET_PATTERN.matcher(contentTypeValue);
        return matcher.find() ? Charset.forName(matcher.group(1)) : CharsetUtil.UTF_8;
    }

    private static final Map<String, Object> beanToMap(Object bean) {
        HashMap map = new HashMap();

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] var3 = beanInfo.getPropertyDescriptors();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                PropertyDescriptor property = var3[var5];
                String key = property.getName();
                if (!key.equals("class")) {
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(bean);
                    if (value != null) {
                        map.put(key, value);
                    }
                }
            }
        } catch (Exception var10) {
            LOG.error("fail to convert bean to map", var10);
        }

        return map;
    }
}
