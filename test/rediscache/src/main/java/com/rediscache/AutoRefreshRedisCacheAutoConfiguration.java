package com.rediscache;

import java.lang.reflect.Method;
import java.util.Objects;

@EnableCaching
@Configuration
@EnableConfigurationProperties({CacheProperties.class, KucoinCacheProperties.class})
@AutoConfigureAfter({RedisConfiguration.class, RedisAutoConfiguration.class})
@AutoConfigureBefore({CacheAutoConfiguration.class})
public class AutoRefreshRedisCacheAutoConfiguration extends CachingConfigurerSupport {
    private final CacheProperties cacheProperties;
    private final KucoinCacheProperties kucoinCacheProperties;

    AutoRefreshRedisCacheAutoConfiguration(CacheProperties cacheProperties, KucoinCacheProperties kucoinCacheProperties) {
        this.cacheProperties = cacheProperties;
        this.kucoinCacheProperties = kucoinCacheProperties;
    }

    @Bean({"secondaryCacheManager"})
    @ConditionalOnMissingBean(
            name = {"secondaryCacheManager"}
    )
    public AutoRefreshRedisCacheManager redisCacheManager(RedisTemplate redisTemplate, ResourceLoader resourceLoader) {
        return new AutoRefreshRedisCacheManager(redisTemplate, RedisCacheWriter.nonLockingRedisCacheWriter(redisTemplate.getConnectionFactory()), this.determineConfiguration(redisTemplate, resourceLoader.getClassLoader()));
    }

    @Bean({"localCacheManager"})
    @ConditionalOnMissingBean(
            name = {"localCacheManager"}
    )
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        String spec = this.cacheProperties.getCaffeine().getSpec();
        if (spec != null) {
            Caffeine<Object, Object> caffeine = Caffeine.from(spec);
            caffeineCacheManager.setCaffeine(caffeine);
        }

        return caffeineCacheManager;
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "kucoin.cache",
            name = {"two-level.enabled"},
            havingValue = "true"
    )
    @ConditionalOnMissingBean(
            name = {"cacheManager"}
    )
    @ConditionalOnBean(
            name = {"localCacheManager", "secondaryCacheManager"},
            value = {CacheManager.class}
    )
    @Primary
    public TwoLevelCacheManager cacheManager(CacheManager localCacheManager, CacheManager secondaryCacheManager, @Autowired(required = false) CacheMessagePublisher publisher) {
        TwoLevelCacheManager manager = new TwoLevelCacheManager(localCacheManager, secondaryCacheManager);
        manager.setMessagePublisher(publisher);
        Notify notify = this.kucoinCacheProperties.getNotify();
        if (notify != null) {
            manager.setTopic(notify.getTopic());
        }

        return manager;
    }

    @Bean({"cacheManager"})
    @ConditionalOnMissingBean(
            name = {"cacheManager"}
    )
    @ConditionalOnProperty(
            prefix = "kucoin.cache",
            name = {"two-level.enabled"},
            havingValue = "false",
            matchIfMissing = true
    )
    @Primary
    public CacheManager noTwoLevelCacheManager(@Qualifier("secondaryCacheManager") RedisCacheManager redisCacheManager) {
        return redisCacheManager;
    }

    @Bean({"redisCachePublisher"})
    @ConditionalOnMissingBean
    @ConditionalOnBean({TwoLevelCacheManager.class})
    @ConditionalOnProperty(
            prefix = "kucoin.cache",
            name = {"notify.topic"}
    )
    public CacheMessagePublisher cacheMessagePublisher(RedisTemplate redisTemplate) {
        return new RedisMessagePublisher(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(
            name = {"cacheManager", "redisCachePublisher"}
    )
    public RedisMessageListener redisMessageListener(RedisTemplate redisTemplate, CacheManager cacheManager) {
        return new RedisMessageListener(redisTemplate, cacheManager);
    }

    @Bean
    @ConditionalOnBean(
            name = {"redisCachePublisher", "redisCachePublisher"},
            value = {RedisMessageListener.class}
    )
    @ConditionalOnProperty(
            prefix = "kucoin.cache",
            name = {"notify.topic"}
    )
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, RedisMessageListener redisCachePublisher) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        Notify notify = this.kucoinCacheProperties.getNotify();
        Object topic;
        if (Objects.equals(notify.getType(), "CHANNEL")) {
            topic = new ChannelTopic(notify.getTopic());
        } else {
            topic = new PatternTopic(notify.getTopic());
        }

        container.addMessageListener(new MessageListenerAdapter(redisCachePublisher, "onMessage"), (Topic)topic);
        return container;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                Object[] var5 = params;
                int var6 = params.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    Object obj = var5[var7];
                    sb.append(String.valueOf(obj));
                }

                return sb.toString();
            }
        };
    }

    @Bean
    @ConditionalOnBean(
            name = {"secondaryCacheManager"},
            value = {AutoRefreshRedisCacheManager.class}
    )
    public InvocationCacheSupport invocationCacheSupport(AutoRefreshRedisCacheManager redisCacheManager) {
        return new InvocationCacheSupport(redisCacheManager);
    }

    @Bean
    @ConditionalOnBean(
            name = {"secondaryCacheManager"},
            value = {AutoRefreshRedisCacheManager.class}
    )
    public AutoRefreshCacheAspect autoRefreshCacheAspect() {
        return new AutoRefreshCacheAspect();
    }

    @Bean
    @ConditionalOnBean(
            name = {"secondaryCacheManager"},
            value = {AutoRefreshRedisCacheManager.class}
    )
    public RedisCacheApplicationContextHelper redisCacheApplicationContextHelper() {
        return new RedisCacheApplicationContextHelper();
    }

    private RedisCacheConfiguration determineConfiguration(RedisTemplate redisTemplate, ClassLoader classLoader) {
        Redis redisProperties = this.cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().serializeKeysWith(SerializationPair.fromSerializer(redisTemplate.getKeySerializer())).serializeValuesWith(SerializationPair.fromSerializer(redisTemplate.getValueSerializer()));
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }

        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixKeysWith(redisProperties.getKeyPrefix());
        }

        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }

        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }

        return config;
    }
}
