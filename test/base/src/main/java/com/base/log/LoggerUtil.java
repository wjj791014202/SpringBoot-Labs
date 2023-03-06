package com.base.log;

import com.base.exception.BaseException;
import com.base.exception.ErrorLevel;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

public class LoggerUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(LoggerUtil.class);
    private static final char RIGHT_TAG = ']';
    private static final char LEFT_TAG = '[';

    public LoggerUtil() {
    }

    public static void debug(Logger logger, String message) {
        if (logger != null) {
            if (logger.isDebugEnabled()) {
                logger.debug(getLogString(message));
            }

        }
    }

    public static void debug(Logger logger, String format, Object... args) {
        if (logger != null) {
            if (logger.isDebugEnabled()) {
                logger.debug(getLogString(String.format(format, args)));
            }

        }
    }

    public static void info(Logger logger, String message) {
        if (logger != null) {
            if (logger.isInfoEnabled()) {
                logger.info(getLogString(message));
            }

        }
    }

    public static void info(Logger logger, String format, Object... args) {
        if (logger != null) {
            if (logger.isInfoEnabled()) {
                logger.info(getLogString(String.format(format, args)));
            }

        }
    }

    public static void warn(Logger logger, String format, Throwable e, Object... args) {
        if (logger != null) {
            logger.warn(getLogString(String.format(format, args)), e);
        }

    }

    public static void warn(Logger logger, String format, Object... args) {
        if (logger != null) {
            logger.warn(getLogString(String.format(format, args)));
        }

    }

    public static void error(Logger logger, String format, Throwable e, Object... args) {
        if (logger != null) {
            logger.error(getLogString(String.format(format, args)), e);
        }

    }

    public static void error(Logger logger, String format, Object... args) {
        if (logger != null) {
            logger.error(getLogString(String.format(format, args)));
        }

    }

    public static void logByErrorLevel(Logger logger, String format, BaseException e, Object... args) {
        if (logger != null) {
            if (ErrorLevel.WARN.equals(e.getErrorCode().getErrorLevel())) {
                logger.warn(getLogString(String.format(format, args) + e.getMessage()), e);
            } else if (ErrorLevel.INFO.equals(e.getErrorCode().getErrorLevel())) {
                if (logger.isInfoEnabled()) {
                    logger.info(getLogString(String.format(format, args) + e.getMessage()), e);
                }
            } else {
                logger.error(getLogString(String.format(format, args) + e.getMessage()), e);
            }

        }
    }

    private static String getLogString(final Object... obj) {
        if (obj == null) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            Object[] var2 = obj;
            int var3 = obj.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Object o = var2[var4];
                sb.append('[');
                sb.append(o);
                sb.append(']');
            }

            return sb.toString();
        }
    }

    public static String getResultSymbol(boolean bool) {
        return bool ? "Y" : "N";
    }
}
