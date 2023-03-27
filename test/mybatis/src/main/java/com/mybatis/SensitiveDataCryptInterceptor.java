/*
 * Copyright 2019 Mek Global Limited.
 */

package com.kucoin.ucenter.core.service.interceptor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kucoin.base.log.LoggerUtil;
import com.kucoin.ucenter.common.dal.dataobject.SensitiveUpdateLogDO;
import com.kucoin.ucenter.common.dal.dataobject.SensitiveUpdateLogDOExample;
import com.kucoin.ucenter.core.service.helper.SecretValueHelper;

/**
 * 数据库数据加解密拦截器 Created by river on 2019/5/9.
 */
@Component
@Intercepts(value = { @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class, CacheKey.class, BoundSql.class }),
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class }) })
public class SensitiveDataCryptInterceptor implements Interceptor {

    @Value("${secret-key.map.account}")
    private String accountKey;

    @Autowired
    private SecretValueHelper secretValueHelper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, Map<String, String>> needCryptColumns = new HashMap<String, Map<String, String>>() {
        {
            put("sensitive_update_log", new HashMap<String, String>() {
                {
                    put("pre_value", accountKey);
                    put("after_value", accountKey);
                }
            });
        }
    };

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        // 入参
        Object parameter = args[1];
        MappedStatement statement = (MappedStatement) args[0];
        BoundSql boundSql = statement.getBoundSql(parameter);
        // 不需要加解密的表直接放过
        if (!tableDataNeedCrypt(boundSql.getSql())) {
            return invocation.proceed();
        }

        Map<String, String> columns = needCryptColumns.get("sensitive_update_log");
        encryptParameters(parameter, columns);
        Object result = invocation.proceed();
        return decryptResult(result);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private boolean tableDataNeedCrypt(String sql) {
        sql = sql.toLowerCase().replace("\n", "");
        for (String table : needCryptColumns.keySet()) {
            if (sql.indexOf(" " + table + " ") > -1)
                return true;
        }
        return false;
    }

    private Object decryptListDO(Object result) {

        List list = (List) result;
        if (list.isEmpty()) {
            return result;
        }
        if (list.get(0) instanceof SensitiveUpdateLogDO) {
            ((List<SensitiveUpdateLogDO>) list).forEach(sensitiveUpdateLogDO -> {
                decryptForDO(sensitiveUpdateLogDO);
            });
        }

        return result;
    }

    private Object decryptForDO(SensitiveUpdateLogDO updateLogDO) {
        if (StringUtils.isNotBlank(updateLogDO.getPreValue())) {
            updateLogDO.setPreValue(secretValueHelper.accountDecrypt(updateLogDO.getPreValue()));
        }
        if (StringUtils.isNotBlank(updateLogDO.getAfterValue())) {
            updateLogDO.setAfterValue(secretValueHelper.accountDecrypt(updateLogDO.getAfterValue()));
        }
        return updateLogDO;
    }

    private void encryptParameters(Object parameter, Map<String, String> columns) {
        // 通过Example查询时候
        if (parameter instanceof SensitiveUpdateLogDOExample) {
            SensitiveUpdateLogDOExample sensitiveUpdateLogDOExample = (SensitiveUpdateLogDOExample) parameter;
            encryptValueForExample(columns, sensitiveUpdateLogDOExample);
        }

        // 入参为DO时候
        if (parameter instanceof SensitiveUpdateLogDO) {
            SensitiveUpdateLogDO sensitiveUpdateLogDO = (SensitiveUpdateLogDO) parameter;
            encryptValueForDO(sensitiveUpdateLogDO);
        }

        // 多参数时候
        if (parameter instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) parameter;
            columns.forEach((columnName, key) -> {
                if (paramMap.containsKey(columnName)) {
                    paramMap.put(columnName, secretValueHelper.accountEncrypt(paramMap.get(columnName).toString()));
                }
            });
            if (paramMap.containsKey("example")) {
                SensitiveUpdateLogDOExample updateLogDOExample = (SensitiveUpdateLogDOExample) paramMap.get("example");
                encryptValueForExample(columns, updateLogDOExample);
            }
            if (paramMap.containsKey("record")) {
                SensitiveUpdateLogDO updateLogDO = (SensitiveUpdateLogDO) paramMap.get("record");
                encryptValueForDO(updateLogDO);
            }
        }
    }

    private void encryptValueForDO(SensitiveUpdateLogDO updateLogDO) {
        if (StringUtils.isNotBlank(updateLogDO.getPreValue())) {
            updateLogDO.setPreValue(secretValueHelper.accountEncrypt(updateLogDO.getPreValue()));
        }
        if (StringUtils.isNotBlank(updateLogDO.getAfterValue())) {
            updateLogDO.setAfterValue(secretValueHelper.accountEncrypt(updateLogDO.getAfterValue()));
        }
    }

    private void encryptValueForExample(Map<String, String> columns, SensitiveUpdateLogDOExample sensitiveUpdateLogDOExample) {
        sensitiveUpdateLogDOExample.getOredCriteria().forEach(criteria -> {
            criteria.getAllCriteria().forEach(criterion -> {
                columns.forEach((key, value) -> {
                    if (criterion.getCondition().toLowerCase().indexOf(key) > -1) {
                        encryptValueForCriterion(criterion, key);
                    }
                });

            });
        });
    }

    private void encryptValueForCriterion(SensitiveUpdateLogDOExample.Criterion criterion, String key) {
        try {
            Field valueField = criterion.getClass().getDeclaredField("value");
            valueField.setAccessible(true);
            Object object = valueField.get(criterion);
            if (object instanceof List) {
                List list = (List) object;
                for (int i = 0; i < list.size(); i++) {
                    String value = list.get(i).toString();
                    if ((key.equals("pre_value")) || (key.equals("after_value"))) {
                        list.set(i, secretValueHelper.accountEncrypt(value));
                    }
                }
            } else {
                String value = object.toString();
                if ((key.equals("pre_value")) || (key.equals("after_value"))) {
                    valueField.set(criterion, secretValueHelper.accountEncrypt(value));
                }
            }
        } catch (NoSuchFieldException e) {
            LoggerUtil.warn(logger, "反射获取属性异常");
        } catch (IllegalAccessException e) {
            LoggerUtil.warn(logger, "反射设置属性异常");
        }
    }

    private Object decryptResult(Object result) {
        if (result == null) {
            return result;
        }
        if (result instanceof SensitiveUpdateLogDO) {
            SensitiveUpdateLogDO sensitiveUpdateLogDO = (SensitiveUpdateLogDO) result;
            return decryptForDO(sensitiveUpdateLogDO);
        }
        if (result instanceof List) {
            return decryptListDO(result);
        }
        return result;
    }

}
