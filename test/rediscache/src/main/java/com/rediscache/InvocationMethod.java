package com.rediscache;

import java.lang.reflect.Method;

public class InvocationMethod {
    private Object key;
    private Object target;
    private Method method;
    private Object[] params;

    public InvocationMethod(Object key, Object target, Method method, Object[] params) {
        this.key = key;
        this.target = target;
        this.method = method;
        this.params = params;
    }

    public int hashCode() {
        int prime = true;
        int result = 1;
        int result = 31 * result + (this.key == null ? 0 : this.key.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            InvocationMethod other = (InvocationMethod)obj;
            if (this.key == null) {
                if (other.key != null) {
                    return false;
                }
            } else if (!this.key.equals(other.key)) {
                return false;
            }

            return true;
        }
    }

    public Object getKey() {
        return this.key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getTarget() {
        return this.target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getParams() {
        return this.params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
