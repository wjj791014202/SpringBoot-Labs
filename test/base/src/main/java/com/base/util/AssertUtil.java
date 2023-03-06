package com.base.util;


import com.kucoin.base.exception.CommonErrorCode;
import com.kucoin.base.exception.ErrorCode;
import com.kucoin.base.exception.ExceptionFactory;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

public class AssertUtil {
    public AssertUtil() {
    }

    public static void assertMatch(AssertUtil.Specification specification, ErrorCode errorCode, Object... args) {
        if (!specification.match()) {
            throw ExceptionFactory.create(errorCode, args);
        }
    }

    public static void assertMatch(AssertUtil.Specification specification, String msg, ErrorCode errorCode, Object... args) {
        if (!specification.match()) {
            throw ExceptionFactory.create(msg, errorCode, args);
        }
    }

    public static void assertMatch(AssertUtil.Specification specification, String msg, Throwable caused, ErrorCode errorCode, Object... args) {
        if (!specification.match()) {
            throw ExceptionFactory.create(msg, caused, errorCode, args);
        }
    }

    public static void assertNotNull(final Object target, String message, Object... args) {
        assertMatch(() -> {
            return target != null;
        }, message, CommonErrorCode.BIZ_ERROR, args);
    }

    public static void assertNotNull(final Object target, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return target != null;
        }, errorCode, args);
    }

    public static void assertNotNull(final Object target, String message, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return target != null;
        }, message, errorCode, args);
    }

    public static void assertNotNull(final Object target, String message, Throwable caused, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return target != null;
        }, message, caused, errorCode, args);
    }

    public static void assertTrue(final boolean target, String message, Object... args) {
        assertMatch(() -> {
            return target;
        }, message, CommonErrorCode.BIZ_ERROR, args);
    }

    public static void assertTrue(final boolean target, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return target;
        }, errorCode, args);
    }

    public static void assertTrue(final boolean target, String message, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return target;
        }, message, errorCode, args);
    }

    public static void assertTrue(final boolean target, String message, Throwable caused, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return target;
        }, message, caused, errorCode, args);
    }

    public static void assertFalse(final boolean target, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return !target;
        }, errorCode, args);
    }

    public static void assertFalse(final boolean target, String message, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return !target;
        }, message, errorCode, args);
    }

    public static void assertFalse(final boolean target, String message, Throwable caused, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return !target;
        }, message, caused, errorCode, args);
    }

    public static void assertEqual(final Object one, final Object another, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return Objects.equals(one, another);
        }, errorCode, args);
    }

    public static void assertEqual(final Object one, final Object another, String message, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return Objects.equals(one, another);
        }, message, errorCode, args);
    }

    public static void assertEqual(final Object one, final Object another, String message, Throwable caused, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return Objects.equals(one, another);
        }, message, caused, errorCode, args);
    }

    public static void assertNotEqual(final Object one, final Object another, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return !Objects.equals(one, another);
        }, errorCode, args);
    }

    public static void assertNotEqual(final Object one, final Object another, String message, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return !Objects.equals(one, another);
        }, message, errorCode, args);
    }

    public static void assertNotEqual(final Object one, final Object another, String message, Throwable caused, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return !Objects.equals(one, another);
        }, message, caused, errorCode, args);
    }

    public static void assertNotBlank(final String target, String message, Object... args) {
        assertMatch(() -> {
            return StringUtils.isNotBlank(target);
        }, message, CommonErrorCode.BIZ_ERROR, args);
    }

    public static void assertNotBlank(final String target, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return StringUtils.isNotBlank(target);
        }, errorCode, args);
    }

    public static void assertNotBlank(final String target, String message, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return StringUtils.isNotBlank(target);
        }, message, errorCode, args);
    }

    public static void assertNotBlank(final String target, String message, Throwable caused, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return StringUtils.isNotBlank(target);
        }, message, caused, errorCode, args);
    }

    public static void assertNotEmpty(final Collection target, String message, Object... args) {
        assertMatch(() -> {
            return CollectionUtils.isNotEmpty(target);
        }, message, CommonErrorCode.BIZ_ERROR, args);
    }

    public static void assertNotEmpty(final Collection target, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return CollectionUtils.isNotEmpty(target);
        }, errorCode, args);
    }

    public static void assertNotEmpty(final Collection target, String message, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return CollectionUtils.isNotEmpty(target);
        }, message, errorCode, args);
    }

    public static void assertNotEmpty(final Collection target, String message, Throwable caused, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return CollectionUtils.isNotEmpty(target);
        }, message, caused, errorCode, args);
    }

    public static void assertNotEmpty(final Map target, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return MapUtils.isNotEmpty(target);
        }, errorCode, args);
    }

    public static void assertNotEmpty(final Map target, String message, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return MapUtils.isNotEmpty(target);
        }, message, errorCode, args);
    }

    public static void assertNotEmpty(final Map target, String message, Throwable caused, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return MapUtils.isNotEmpty(target);
        }, message, caused, errorCode, args);
    }

    public static void assertEmpty(final Collection target, String message, Object... args) {
        assertMatch(() -> {
            return CollectionUtils.isEmpty(target);
        }, message, CommonErrorCode.BIZ_ERROR, args);
    }

    public static void assertEmpty(final Collection target, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return CollectionUtils.isEmpty(target);
        }, errorCode, args);
    }

    public static void assertEmpty(final Collection target, String message, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return CollectionUtils.isEmpty(target);
        }, message, errorCode, args);
    }

    public static void assertEmpty(final Collection target, String message, Throwable caused, ErrorCode errorCode, Object... args) {
        assertMatch(() -> {
            return CollectionUtils.isEmpty(target);
        }, message, caused, errorCode, args);
    }

    public static void assertError(ErrorCode errorCode, Object... args) {
        throw ExceptionFactory.create(errorCode, args);
    }

    public static void assertError(String message, ErrorCode errorCode, Object... args) {
        throw ExceptionFactory.create(message, errorCode, args);
    }

    public static void assertError(String message, Throwable caused, ErrorCode errorCode, Object... args) {
        throw ExceptionFactory.create(message, caused, errorCode, args);
    }

    public interface Specification {
        boolean match();
    }
}

