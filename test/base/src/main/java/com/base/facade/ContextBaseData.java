package com.base.facade;


import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class ContextBaseData {

    private Date curDate;
    private Map<String, Object> data = new HashMap();

    public ContextBaseData() {
    }

    public Date getCurDate() {
        return this.curDate;
    }

    public void setCurDate(Date curDate) {
        this.curDate = curDate;
    }

    public <T> T get(String key) {
        return (T) this.data.get(key);
    }

    public <T> T put(String key, T value) {
        return (T) this.data.put(key, value);
    }

    public void putAll(Map<String, Object> map) {
        this.data.putAll(map);
    }

    public Map<String, Object> getAll() {
        return new HashMap(this.data);
    }

    public void compose(ContextBaseData composeData, boolean replace) {
        Map<String, Object> composeMap = composeData.getAll();
        if (replace) {
            this.data.putAll(composeMap);
        } else {
            Iterator var4 = composeMap.entrySet().iterator();

            while (var4.hasNext()) {
                Entry<String, Object> entry = (Entry) var4.next();
                this.data.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }

        this.curDate = replace && composeData.getCurDate() != null ? composeData.getCurDate() : this.curDate;
    }
}

