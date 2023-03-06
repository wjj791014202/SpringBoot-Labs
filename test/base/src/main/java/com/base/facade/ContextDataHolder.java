package com.base.facade;


import java.util.Date;

public class ContextDataHolder {
    private static final ThreadLocal<ContextBaseData> HOLDER = new InheritableThreadLocal();

    public ContextDataHolder() {
    }

    public static void initial() {
        ContextBaseData baseData = new ContextBaseData();
        baseData.setCurDate(new Date());
        HOLDER.set(baseData);
    }

    public static void initial(ContextBaseData contextData) {
        HOLDER.set(contextData);
    }

    public static <T extends ContextBaseData> T getContext() {
        return (T) HOLDER.get();
    }

    public static void clean() {
        HOLDER.remove();
    }

    public static ThreadLocal<ContextBaseData> getHolder() {
        return HOLDER;
    }
}

