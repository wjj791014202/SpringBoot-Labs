package com.log;


import com.kucoin.base.exception.BaseException;
import java.io.Serializable;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class LogInfo implements Serializable {
    private static final long serialVersionUID = -28692878927802L;
    private String className;
    private String methodName;
    private long startTime;
    private long endTime;
    private boolean exceptional;
    private Throwable throwable;
    private Object[] args;
    private Object result;
    private String bizCode;
    private String userId;
    private Object keyProps;

    public LogInfo() {
    }

    public String printDigestLog() {
        boolean isSuccess = true;
        String resultCode = "";
        if (this.exceptional) {
            isSuccess = false;
            resultCode = getErrorCode(this.throwable);
        } else {
            isSuccess = true;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("(");
        sb.append((String)StringUtils.defaultIfBlank(this.bizCode, "-"));
        sb.append(")");
        sb.append("(");
        sb.append((String)StringUtils.defaultIfBlank(this.className, "-"));
        sb.append(".");
        sb.append((String)StringUtils.defaultIfBlank(this.methodName, "-"));
        sb.append(",");
        sb.append((String)StringUtils.defaultIfBlank(isSuccess ? "Y" : "N", "-"));
        sb.append(",");
        sb.append(this.getElapseTime());
        sb.append("ms");
        sb.append(")");
        sb.append("(");
        sb.append((String)StringUtils.defaultIfBlank(resultCode, "-"));
        sb.append(")");
        sb.append("(");
        sb.append(this.result == null ? "Y" : "N");
        sb.append(")");
        sb.append("]");
        sb.append("[");
        sb.append((String)StringUtils.defaultIfBlank(this.userId, "-"));
        sb.append("]");
        sb.append("[");
        sb.append(this.keyProps == null ? "-" : this.keyProps);
        sb.append("]");
        return sb.toString();
    }

    public long getElapseTime() {
        return this.endTime - this.startTime;
    }

    public String printInfoLog() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append((String)StringUtils.defaultIfBlank(this.bizCode, "-"));
        sb.append(",");
        sb.append((String)StringUtils.defaultIfBlank(this.className, "-"));
        sb.append(".");
        sb.append((String)StringUtils.defaultIfBlank(this.methodName, "-"));
        sb.append("]");
        sb.append("[");
        sb.append("请求参数");
        sb.append("]");
        sb.append("[");
        Object[] var2 = (Object[])((Object[])ObjectUtils.defaultIfNull(this.args, new Object[0]));
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Object obj = var2[var4];
            if (null != obj) {
                sb.append(obj.toString());
                sb.append(",");
            }
        }

        sb.append("]");
        sb.append("   ");
        sb.append("[");
        sb.append("返回结果");
        sb.append("]");
        sb.append("[");
        sb.append(this.result == null ? null : ToStringBuilder.reflectionToString(this.result, ToStringStyle.SHORT_PREFIX_STYLE));
        sb.append("]");
        return sb.toString();
    }

    public static String getErrorCode(Throwable t) {
        if (!(t instanceof BaseException)) {
            return "";
        } else {
            return ((BaseException)t).getErrorCode() == null ? "" : ((BaseException)t).getErrorCode().getCode();
        }
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }

    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(final long endTime) {
        this.endTime = endTime;
    }

    public void setExceptional(final boolean exceptional) {
        this.exceptional = exceptional;
    }

    public void setArgs(final Object[] args) {
        this.args = args;
    }

    public void setResult(final Object result) {
        this.result = result;
    }

    public void setBizCode(final String bizCode) {
        this.bizCode = bizCode;
    }

    public Object getKeyProps() {
        return this.keyProps;
    }

    public void setKeyProps(Object keyProps) {
        this.keyProps = keyProps;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}

