package com.redisson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Sentinel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.ReflectionUtils;

@Configuration
@ConditionalOnClass({Redisson.class})
@AutoConfigureAfter({RedisAutoConfiguration.class})
@EnableConfigurationProperties({RedissonProperties.class})
public class RedissonAutoConfiguration {
    @Autowired
    private RedissonProperties redissonProperties;
    @Autowired
    private RedisProperties redisProperties;
    @Autowired
    private ApplicationContext ctx;

    public RedissonAutoConfiguration() {
    }

    @Bean
    @ConditionalOnMissingBean({RedisConnectionFactory.class})
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
        return new RedissonConnectionFactory(redisson);
    }

    @Bean(
            destroyMethod = "shutdown"
    )
    @ConditionalOnMissingBean({RedissonClient.class})
    public RedissonClient redisson() throws IOException {
        Config config = null;
        Method clusterMethod = ReflectionUtils.findMethod(RedisProperties.class, "getCluster");
        Method timeoutMethod = ReflectionUtils.findMethod(RedisProperties.class, "getTimeout");
        Object timeoutValue = ReflectionUtils.invokeMethod(timeoutMethod, this.redisProperties);
        int timeout;
        Method nodesMethod;
        if (!(timeoutValue instanceof Integer)) {
            nodesMethod = ReflectionUtils.findMethod(timeoutValue.getClass(), "toMillis");
            timeout = ((Long)ReflectionUtils.invokeMethod(nodesMethod, timeoutValue)).intValue();
        } else {
            timeout = (Integer)timeoutValue;
        }

        if (this.redissonProperties.getConfig() != null) {
            try {
                InputStream is = this.getConfigStream();
                config = Config.fromJSON(is);
            } catch (IOException var10) {
                try {
                    InputStream is = this.getConfigStream();
                    config = Config.fromYAML(is);
                } catch (IOException var9) {
                    throw new IllegalArgumentException("Can't parse config", var9);
                }
            }
        } else if (this.redisProperties.getSentinel() != null) {
            nodesMethod = ReflectionUtils.findMethod(Sentinel.class, "getNodes");
            Object nodesValue = ReflectionUtils.invokeMethod(nodesMethod, this.redisProperties.getSentinel());
            String[] nodes;
            if (nodesValue instanceof String) {
                nodes = ((String)nodesValue).split(",");
            } else {
                nodes = (String[])((List)nodesValue).toArray(new String[((List)nodesValue).size()]);
            }

            config = new Config();
            ((SentinelServersConfig)config.useSentinelServers().setMasterName(this.redisProperties.getSentinel().getMaster()).addSentinelAddress(nodes).setDatabase(this.redisProperties.getDatabase()).setConnectTimeout(timeout)).setPassword(this.redisProperties.getPassword());
        } else {
            Method method;
            if (clusterMethod != null && ReflectionUtils.invokeMethod(clusterMethod, this.redisProperties) != null) {
                Object clusterObject = ReflectionUtils.invokeMethod(clusterMethod, this.redisProperties);
                method = ReflectionUtils.findMethod(clusterObject.getClass(), "getNodes");
                List<String> nodesObject = (List)ReflectionUtils.invokeMethod(method, clusterObject);
                nodesObject = (List)nodesObject.stream().map((node) -> {
                    return node.startsWith("redis://") ? node : "redis://" + node;
                }).collect(Collectors.toList());
                config = new Config();
                ((ClusterServersConfig)config.useClusterServers().addNodeAddress((String[])nodesObject.toArray(new String[nodesObject.size()])).setConnectTimeout(timeout)).setPassword(this.redisProperties.getPassword());
            } else {
                config = new Config();
                String prefix = "redis://";
                method = ReflectionUtils.findMethod(RedisProperties.class, "isSsl");
                if (method != null && (Boolean)ReflectionUtils.invokeMethod(method, this.redisProperties)) {
                    prefix = "rediss://";
                }

                ((SingleServerConfig)config.useSingleServer().setAddress(prefix + this.redisProperties.getHost() + ":" + this.redisProperties.getPort()).setConnectTimeout(timeout)).setDatabase(this.redisProperties.getDatabase()).setPassword(this.redisProperties.getPassword());
            }
        }

        return Redisson.create(config);
    }

    protected InputStream getConfigStream() throws IOException {
        Resource resource = this.ctx.getResource(this.redissonProperties.getConfig());
        InputStream is = resource.getInputStream();
        return is;
    }
}
