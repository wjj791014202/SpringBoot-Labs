package com.base.facade;

import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FacadeBizTypeStreamSupport<T, B> extends FacadeProcessStreamSupport<T, B> {

    private B bizType;

    FacadeBizTypeStreamSupport(Stream<T> stream, B bizType) {
        super(stream, bizType);
        this.stream = stream.peek((t) -> {
            this.initContextInternal();
        });
        this.bizType = bizType;
    }

    private void initContextInternal() {
        ContextBaseData contextData = ContextDataHolder.getContext();
        if (contextData == null) {
            ContextBaseData baseData = new ContextBaseData();
            baseData.setCurDate(new Date());
            ContextDataHolder.initial(baseData);
        }
    }

    public FacadeProcessStreamSupport<T, B> initContext(Supplier<? extends ContextBaseData> supplier) {
        this.stream = this.stream.peek((t) -> {
            ContextBaseData contextData = (ContextBaseData) supplier.get();
            if (contextData != null) {
                ContextBaseData data = ContextDataHolder.getContext();
                contextData.compose(data, false);
                ContextDataHolder.initial(contextData);
            }

        });
        return this;
    }

    public FacadeProcessStreamSupport<T, B> initContext(Map<String, Object> values) {
        this.stream = this.stream.peek((t) -> {
            ContextBaseData data = ContextDataHolder.getContext();
            data.putAll(values);
        });
        return this;
    }
}

