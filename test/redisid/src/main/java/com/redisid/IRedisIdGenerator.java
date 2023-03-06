package com.redisid;

import java.util.List;

public interface IRedisIdGenerator extends IIdGenerator {
    List<Long> nextIdList(String counterName, int count);

    List<String> nextStringIdList(String counterName, int count);

    boolean destroy(String counterName);
}
