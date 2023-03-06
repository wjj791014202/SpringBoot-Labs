package com.request;

import feign.MethodMetadata;
import feign.form.ContentType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor.AnnotatedParameterContext;

public class FormBodyParameterProcessor implements AnnotatedParameterProcessor {
    private static final String FORM_PARAM_PREFIX = "FORM_PARAM_PREFIX_";
    private static final Class<FormBody> ANNOTATION = FormBody.class;

    public FormBodyParameterProcessor() {
    }

    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    public boolean processArgument(AnnotatedParameterContext context, Annotation annotation, Method method) {
        String name = "FORM_PARAM_PREFIX_" + context.getParameterIndex();
        context.setParameterName(name);
        MethodMetadata data = context.getMethodMetadata();
        String varName = '{' + name + '}';
        if (!data.template().url().contains(varName) && !this.searchMapValues(data.template().queries(), varName) && !this.searchMapValues(data.template().headers(), varName)) {
            data.formParams().add(name);
        }

        data.template().header("Content-Type", new String[]{ContentType.URLENCODED.getHeader()});
        return true;
    }

    private <K, V> boolean searchMapValues(Map<K, Collection<V>> map, V search) {
        Collection<Collection<V>> values = map.values();
        if (values == null) {
            return false;
        } else {
            Iterator var4 = values.iterator();

            Collection entry;
            do {
                if (!var4.hasNext()) {
                    return false;
                }

                entry = (Collection)var4.next();
            } while(!entry.contains(search));

            return true;
        }
    }
}
