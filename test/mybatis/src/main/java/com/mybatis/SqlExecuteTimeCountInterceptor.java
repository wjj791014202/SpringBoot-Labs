package com.kucoin.ucenter.core.service.interceptor;


import com.kucoin.ucenter.common.constants.DataDogMetrics;
import com.timgroup.statsd.StatsDClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Statement;
import java.util.Properties;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})})
@Component
@Slf4j
public class SqlExecuteTimeCountInterceptor implements Interceptor {
    @Autowired
    private StatsDClient statsDClient;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
        StatementHandler delegate = (StatementHandler) getFieldValue(handler, "delegate");
        MappedStatement mappedStatement = (MappedStatement) getFieldValue(delegate, "mappedStatement");
        String sqlId = mappedStatement.getId();
        long startTime = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            statsDClient.histogram(DataDogMetrics.MYBATIS_SQL_KEY, System.currentTimeMillis() - startTime, DataDogMetrics.MYBATIS_SQL_ID + sqlId);
        }
    }

    public Object getFieldValue(Object object, String fieldName) {
        Field field = getDeclaredField(object, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        }

        makeAccessible(field);
        Object result = null;
        try {
            result = field.get(object);
        } catch (IllegalAccessException e) {
            log.error("get field {} value failed", field.getName());
        }

        return result;
    }

    private Field getDeclaredField(Object object, String filedName) {
        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(filedName);
            } catch (NoSuchFieldException e) {
                //Field is not defined in current class
            }
        }
        return null;
    }

    private void makeAccessible(Field field) {
        if (!Modifier.isPublic(field.getModifiers())) {
            field.setAccessible(true);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}