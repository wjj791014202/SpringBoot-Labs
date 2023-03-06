package com.mybatis;

import java.lang.reflect.Field;
import java.util.Properties;

@Intercepts({@Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
)})
public class DbInsertIntecerptor implements Interceptor {
    private static final Logger LOG = LoggerFactory.getLogger(DbInsertIntecerptor.class);
    private static final String ID = "id";
    private static final String DOMAIN_ID = "domainId";
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";

    public DbInsertIntecerptor() {
    }

    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement)invocation.getArgs()[0];
        if (SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType())) {
            this.insertPreHandler(invocation.getArgs()[1]);
        }

        return invocation.proceed();
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
    }

    private void insertPreHandler(Object param) {
        try {
            Field[] fields = param.getClass().getDeclaredFields();
            Field[] var3 = fields;
            int var4 = fields.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Field field = var3[var5];
                String var7 = field.getName();
                byte var8 = -1;
                switch(var7.hashCode()) {
                    case -1949194674:
                        if (var7.equals("updatedAt")) {
                            var8 = 3;
                        }
                        break;
                    case 3355:
                        if (var7.equals("id")) {
                            var8 = 0;
                        }
                        break;
                    case 598371643:
                        if (var7.equals("createdAt")) {
                            var8 = 2;
                        }
                        break;
                    case 1129430271:
                        if (var7.equals("domainId")) {
                            var8 = 1;
                        }
                }

                switch(var8) {
                    case 0:
                        if (field.getType().equals(String.class)) {
                            field.setAccessible(true);
                            if (field.get(param) == null) {
                                field.set(param, ObjectId.getString());
                            }
                        }
                        break;
                    case 1:
                        field.setAccessible(true);
                        if (field.get(param) == null) {
                            if (ChainRequestContext.getCurrentContext() != null && ChainRequestContext.getCurrentContext().get("X-DOMAIN-ID") != null) {
                                field.set(param, ChainRequestContext.getCurrentContext().get("X-DOMAIN-ID"));
                            } else {
                                LOG.warn("fail to get domain_id from chain context, use default 'kucoin' instead, object is {}", JSONObject.toJSONString(param));
                                field.set(param, "kucoin");
                            }
                        }
                        break;
                    case 2:
                    case 3:
                        field.setAccessible(true);
                        if (field.get(param) != null) {
                            field.set(param, (Object)null);
                        }
                }
            }
        } catch (Exception var9) {
            LOG.error("fail to process insert pre handler", var9);
        }

    }
}

