package com.rediscache;


import java.time.Duration;

@EnableConfigurationProperties({KucoinCacheProperties.class})
@Configuration
public class RedisConfiguration {
    @Autowired
    private KucoinCacheProperties kucoinCacheProperties;

    public RedisConfiguration() {
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        String var4 = this.kucoinCacheProperties.getSerializer().getValue();
        byte var5 = -1;
        switch(var4.hashCode()) {
            case -763714423:
                if (var4.equals("gzip_fastjson")) {
                    var5 = 2;
                }
                break;
            case 3301053:
                if (var4.equals("kryo")) {
                    var5 = 1;
                }
                break;
            case 969291588:
                if (var4.equals("fastjson")) {
                    var5 = 0;
                }
        }

        Object valueRedisSerializer;
        switch(var5) {
            case 0:
                valueRedisSerializer = new GenericFastJsonRedisSerializer();
                break;
            case 1:
                valueRedisSerializer = new KryoRedisSerializer();
                break;
            case 2:
                valueRedisSerializer = new GzipRedisSerializer(new GenericFastJsonRedisSerializer());
                break;
            default:
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
                objectMapper.enableDefaultTyping(DefaultTyping.NON_FINAL);
                objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                valueRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        }

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer((RedisSerializer)valueRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer((RedisSerializer)valueRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(
            name = {"lettuceClientConfigurationBuilderCustomizer"}
    )
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder().enableAllAdaptiveRefreshTriggers().adaptiveRefreshTriggersTimeout(Duration.ofSeconds(this.kucoinCacheProperties.getLettuce().getRefreshTriggersTimeoutInSeconds())).enablePeriodicRefresh(Duration.ofSeconds(this.kucoinCacheProperties.getLettuce().getPeriodicRefreshInSeconds())).build();
        return (builder) -> {
            builder.clientOptions(ClusterClientOptions.builder().topologyRefreshOptions(clusterTopologyRefreshOptions).validateClusterNodeMembership(false).build());
        };
    }
}
