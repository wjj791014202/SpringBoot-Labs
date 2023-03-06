package com.base.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class Event implements Serializable, Cloneable {
    private static final long serialVersionUID = -4140829398077396387L;
    private String id;
    private String type;
    private Object data;
    private Long timestamp;
    private String from;

    public static Event build(String id, String type, Object data) {
        Event event = new Event();
        event.id = id;
        event.type = type;
        event.data = data;
        event.timestamp = System.currentTimeMillis();
        return event;
    }

    public static Event build(String type, Object data) {
        return build(ObjectId.getString(), type, data);
    }

    public <T> T getData(Class<T> clazz) {
        if (this.data instanceof Map) {
            return JSON.toJavaObject((JSON)JSON.toJSON(this.data), clazz);
        } else {
            return this.data instanceof JSON ? JSON.toJavaObject((JSON)this.data, clazz) : this.data;
        }
    }

    public <T> List<T> getDataList(Class<T> clazz) {
        if (!(this.data instanceof JSONArray)) {
            return (List)this.data;
        } else {
            JSONArray dataArray = (JSONArray)this.data;
            List<T> dataList = new ArrayList(dataArray.size());
            int i = 0;

            for(int size = dataArray.size(); i < size; ++i) {
                JSON jsonData = (JSON)dataArray.get(i);
                dataList.add(JSON.toJavaObject(jsonData, clazz));
            }

            return dataList;
        }
    }

    public static Event parseFrom(String jsonString) {
        return (Event)JSON.parseObject(jsonString, Event.class);
    }

    public Event clone() {
        try {
            return (Event)super.clone();
        } catch (CloneNotSupportedException var2) {
            throw new RuntimeException(var2);
        }
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }

    public Event() {
    }

    public String getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public Object getData() {
        return this.data;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public String getFrom() {
        return this.from;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    public void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setFrom(final String from) {
        this.from = from;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Event)) {
            return false;
        } else {
            Event other = (Event)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label71: {
                    Object this$id = this.getId();
                    Object other$id = other.getId();
                    if (this$id == null) {
                        if (other$id == null) {
                            break label71;
                        }
                    } else if (this$id.equals(other$id)) {
                        break label71;
                    }

                    return false;
                }

                Object this$type = this.getType();
                Object other$type = other.getType();
                if (this$type == null) {
                    if (other$type != null) {
                        return false;
                    }
                } else if (!this$type.equals(other$type)) {
                    return false;
                }

                label57: {
                    Object this$data = this.getData();
                    Object other$data = other.getData();
                    if (this$data == null) {
                        if (other$data == null) {
                            break label57;
                        }
                    } else if (this$data.equals(other$data)) {
                        break label57;
                    }

                    return false;
                }

                Object this$timestamp = this.getTimestamp();
                Object other$timestamp = other.getTimestamp();
                if (this$timestamp == null) {
                    if (other$timestamp != null) {
                        return false;
                    }
                } else if (!this$timestamp.equals(other$timestamp)) {
                    return false;
                }

                Object this$from = this.getFrom();
                Object other$from = other.getFrom();
                if (this$from == null) {
                    if (other$from == null) {
                        return true;
                    }
                } else if (this$from.equals(other$from)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Event;
    }

    public int hashCode() {
        int PRIME = true;
        int result = 1;
        Object $id = this.getId();
        int result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        Object $timestamp = this.getTimestamp();
        result = result * 59 + ($timestamp == null ? 43 : $timestamp.hashCode());
        Object $from = this.getFrom();
        result = result * 59 + ($from == null ? 43 : $from.hashCode());
        return result;
    }

    public String toString() {
        return "Event(id=" + this.getId() + ", type=" + this.getType() + ", data=" + this.getData() + ", timestamp=" + this.getTimestamp() + ", from=" + this.getFrom() + ")";
    }
}

