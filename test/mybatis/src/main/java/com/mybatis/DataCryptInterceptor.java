/*
 * Copyright 2019 Mek Global Limited.
 */

package com.kucoin.ucenter.core.service.interceptor;

import com.kucoin.base.log.LoggerUtil;
import com.kucoin.ucenter.common.dal.dataobject.UserDO;
import com.kucoin.ucenter.common.dal.dataobject.UserDOExample;
import com.kucoin.ucenter.core.service.helper.SecretValueHelper;
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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * 数据库数据加解密拦截器 Created by chenshiwei on 2018/12/28.
 */
@Component
@Intercepts(value = {@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class})})
public class DataCryptInterceptor implements Interceptor {

    @Value("${secret-key.map.account}")
    private String accountKey;

    @Autowired
    private SecretValueHelper secretValueHelper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Pattern numberPattern = Pattern.compile("^[\\d]*$");

    private Map<String, Map<String, String>> needCryptColumns = new HashMap<String, Map<String, String>>() {
        {
            put("user", new HashMap<String, String>() {
                {
                    put("email", accountKey);
                    put("phone", accountKey);
                    put("sub_name", accountKey);
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

        Map<String, String> columns = needCryptColumns.get("user");
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
            if (sql.indexOf(" " + table + " ") > -1 || sql.endsWith(" " + table))
                return true;
        }
        return false;
    }

    private Object decryptListDO(Object result) {

        List list = (List) result;
        if (list.isEmpty()) {
            return result;
        }
        if (list.get(0) instanceof UserDO) {
            ((List<UserDO>) list).forEach(userDO -> {
                decryptForDO(userDO);
            });
        }

        return result;
    }

    private Object decryptForDO(UserDO userDO) {
        if (StringUtils.isNotBlank(userDO.getEmail()) && !userDO.getEmail().contains("@")) {
            userDO.setEmail(secretValueHelper.accountDecrypt(userDO.getEmail()));
        }
        if (StringUtils.isNotBlank(userDO.getPhone()) && !numberPattern.matcher(userDO.getPhone()).matches()) {
            userDO.setPhone(secretValueHelper.accountDecrypt(userDO.getPhone()));
        }
        if (StringUtils.isNotBlank(userDO.getSubName())) {
            userDO.setSubName(secretValueHelper.accountDecrypt(userDO.getSubName()));
        }
        return userDO;
    }

    private void encryptParameters(Object parameter, Map<String, String> columns) {
        // 通过Example查询时候
        if (parameter instanceof UserDOExample) {
            UserDOExample userDOExample = (UserDOExample) parameter;
            encryptValueForExample(columns, userDOExample);
        }

        // 入参为DO时候
        if (parameter instanceof UserDO) {
            UserDO userDO = (UserDO) parameter;
            encryptValueForDO(userDO);
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
                UserDOExample userDOExample = (UserDOExample) paramMap.get("example");
                encryptValueForExample(columns, userDOExample);
            }
            if (paramMap.containsKey("record")) {
                UserDO userDO = (UserDO) paramMap.get("record");
                encryptValueForDO(userDO);
            }
        }
    }

    private void encryptValueForDO(UserDO userDO) {
        if (StringUtils.isNotBlank(userDO.getEmail()) && userDO.getEmail().contains("@")) {
            userDO.setEmail(secretValueHelper.accountEncrypt(userDO.getEmail()));
        }
        if (StringUtils.isNotBlank(userDO.getPhone()) && numberPattern.matcher(userDO.getPhone()).matches()) {
            userDO.setPhone(secretValueHelper.accountEncrypt(userDO.getPhone()));
        }
        if (StringUtils.isNotBlank(userDO.getSubName())) {
            userDO.setSubName(secretValueHelper.accountEncrypt(userDO.getSubName()));
        }
    }

    private void encryptValueForExample(Map<String, String> columns, UserDOExample userDOExample) {
        userDOExample.getOredCriteria().forEach(criteria -> {
            criteria.getAllCriteria().forEach(criterion -> {
                columns.forEach((key, value) -> {
                    if (criterion.getCondition().toLowerCase().indexOf(key) > -1) {
                        encryptValueForCriterion(criterion, key);
                    }
                });

            });
        });
    }

    private void encryptValueForCriterion(UserDOExample.Criterion criterion, String key) {
        try {
            Field valueField = criterion.getClass().getDeclaredField("value");
            valueField.setAccessible(true);
            Object object = valueField.get(criterion);
            if (object instanceof List) {
                List list = (List) object;
                for (int i = 0; i < list.size(); i++) {
                    String value = list.get(i).toString();
                    if ((key.equals("email") && value.contains("@"))
                            || (key.equals("phone") && numberPattern.matcher(value).matches()) || (key.equals("sub_name"))) {
                        list.set(i, secretValueHelper.accountEncrypt(value));
                    }
                }
            } else {
                String value = object.toString();
                if ((key.equals("email") && value.contains("@"))
                        || (key.equals("phone") && numberPattern.matcher(value).matches()) || (key.equals("sub_name"))) {
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
        if (result instanceof UserDO) {
            UserDO userDO = (UserDO) result;
            return decryptForDO(userDO);
        }
        if (result instanceof List) {
            return decryptListDO(result);
        }
        return result;
    }

}
