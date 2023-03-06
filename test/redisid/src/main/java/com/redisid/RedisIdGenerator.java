package com.redisid;


import com.google.common.base.Preconditions;
import com.kucoin.starter.id.config.RedisIdProperties;
import com.kucoin.starter.id.lock.ILockService;
import com.kucoin.starter.id.util.NumberUtil;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisIdGenerator implements IRedisIdGenerator {
    private static final Logger log = LoggerFactory.getLogger(RedisIdGenerator.class);
    private static final long BASE_EPOCH = 1554279692554L;
    private static final long ID_THRESHOLD = 100000000L;
    private static final long DELTA = 1L;
    private static final String ID_KEY_LOCK_SUFFIX = ":lock";
    private RedisTemplate<String, String> redisTemplate;
    private RedisIdProperties redisIdProperties;
    private ILockService lockService;
    private RedisSerializer<String> keySerializer = new StringRedisSerializer();
    private RedisSerializer<Long> valueSerializer = new IdRedisSerializer();
    private ConcurrentMap<String, Object> lockMap = new ConcurrentHashMap();
    private ConcurrentMap<String, Long> lastIdMap = new ConcurrentHashMap();

    public RedisIdGenerator(RedisTemplate<String, String> redisTemplate, RedisIdProperties redisIdProperties, ILockService lockService) {
        this.redisTemplate = redisTemplate;
        this.redisIdProperties = redisIdProperties;
        this.lockService = lockService;
    }

    public long nextId(String counterName) {
        String key = this.generateKey(counterName);
        synchronized(this.fetchLock(counterName)) {
            return this.generateId(key, counterName);
        }
    }

    public List<Long> nextIdList(String counterName, int count) {
        String key = this.generateKey(counterName);
        synchronized(this.fetchLock(counterName)) {
            return this.generateIdList(key, counterName, count);
        }
    }

    private Object fetchLock(String counterName) {
        Object lock = this.lockMap.putIfAbsent(counterName, counterName);
        if (lock == null) {
            lock = counterName;
        }

        return lock;
    }

    private long generateId(String key, String counterName) {
        long id = this.increment(key);
        long lastId = (Long)this.lastIdMap.getOrDefault(counterName, 100000000L);

        while(id <= lastId) {
            log.info("try to init counter {}, id {} <= lastId {}", new Object[]{counterName, id, lastId});
            String lockKey = key + ":lock";
            boolean locked = false;

            try {
                this.lockService.lock(lockKey);
                locked = true;
                this.eliminateTimeLag();
                if ((id = this.increment(key)) <= lastId) {
                    id = this.generateInitialId();
                    log.info("init generator {} with id {}", counterName, id);
                    this.setId(key, id);
                }
            } catch (InterruptedException var13) {
                throw new RuntimeException(var13);
            } finally {
                if (locked) {
                    this.lockService.unlock(lockKey);
                }

            }
        }

        this.lastIdMap.put(counterName, id);
        return id;
    }

    private List<Long> generateIdList(String key, String counterName, int count) {
        List<Long> idList = this.incrementList(key, count);
        long lastId = (Long)this.lastIdMap.getOrDefault(counterName, 100000000L);

        while((Long)idList.get(0) <= lastId) {
            log.info("try to init counter {}, id {} <= lastId {}", new Object[]{counterName, idList.get(0), lastId});
            String lockKey = key + ":lock";
            boolean locked = false;

            try {
                this.lockService.lock(lockKey);
                locked = true;
                this.eliminateTimeLag();
                idList = this.incrementList(key, count);
                if ((Long)idList.get(0) <= lastId) {
                    long id = this.generateInitialId();
                    log.info("init generator {} with id {}", counterName, id);
                    this.setId(key, id);
                    idList = this.incrementList(key, count);
                }
            } catch (InterruptedException var14) {
                throw new RuntimeException(var14);
            } finally {
                if (locked) {
                    this.lockService.unlock(lockKey);
                }

            }
        }

        this.lastIdMap.put(counterName, idList.get(idList.size() - 1));
        return idList;
    }

    private long increment(String key) {
        Long id = this.redisTemplate.opsForValue().increment(key, 1L);

        while(id == null || id > this.generateInitialId()) {
            long retryWait = this.redisIdProperties.getRetryWait();
            if (id == null) {
                log.warn("redis increment failed, wait {} ms and then retry", retryWait);
            } else {
                log.warn("time moved backward or id is out if limit, wait {} ms and then retry", retryWait);
            }

            this.waitQuietly(retryWait);
            if (id == null) {
                id = this.redisTemplate.opsForValue().increment(key, 1L);
            }
        }

        return id;
    }

    private List<Long> incrementList(String key, int count) {
        List idList = this.batchGenerateIdList(key, count);

        while(idList.isEmpty() || (Long)idList.get(idList.size() - 1) > this.generateInitialId()) {
            long retryWait = this.redisIdProperties.getRetryWait();
            if (idList.isEmpty()) {
                log.warn("redis incrementList failed, wait {} ms and then retry", retryWait);
            } else {
                log.warn("time moved backward or id is out if limit, wait {} ms and then retry", retryWait);
            }

            this.waitQuietly(retryWait);
            if (idList.isEmpty()) {
                idList = this.batchGenerateIdList(key, count);
            }
        }

        return idList;
    }

    private List<Long> batchGenerateIdList(String key, int count) {
        final byte[] keyBytes = this.keySerializer.serialize(key);
        List<Object> idObjectList = this.redisTemplate.executePipelined(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                for(int i = 0; i < count; ++i) {
                    connection.incr(keyBytes);
                }

                return null;
            }
        });
        return (List)idObjectList.stream().map((idObject) -> {
            return (Long)idObject;
        }).collect(Collectors.toList());
    }

    private long generateInitialId() {
        return (System.currentTimeMillis() - 1554279692554L) * this.redisIdProperties.getMultipler();
    }

    private void setId(String key, long id) {
        final byte[] keyBytes = this.keySerializer.serialize(key);
        final byte[] valueBytes = this.valueSerializer.serialize(id);
        final Expiration expiration = Expiration.from(this.redisIdProperties.getCounterMaxAge(), TimeUnit.MILLISECONDS);
        this.redisTemplate.execute(new RedisCallback<Void>() {
            public Void doInRedis(RedisConnection connection) throws DataAccessException {
                connection.set(keyBytes, valueBytes, expiration, SetOption.UPSERT);
                return null;
            }
        });
    }

    private String generateKey(String counterName) {
        return this.redisIdProperties.getKeyPrefix() + counterName;
    }

    private void eliminateTimeLag() {
        long maxLagTime = this.redisIdProperties.getMaxLagTime();
        if (maxLagTime > 0L) {
            this.waitQuietly(maxLagTime);
        }

    }

    private void waitQuietly(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException var4) {
        }

    }

    public String nextStringId(String counterName) {
        return NumberUtil.toString(this.nextId(counterName), NumberUtil.MAX_RADIX);
    }

    public List<String> nextStringIdList(String counterName, int count) {
        List<Long> idList = this.nextIdList(counterName, count);
        return (List)idList.stream().map((id) -> {
            return NumberUtil.toString(id, NumberUtil.MAX_RADIX);
        }).collect(Collectors.toList());
    }

    public boolean destroy(String counterName) {
        String key = this.generateKey(counterName);
        return this.redisTemplate.delete(key);
    }
}

