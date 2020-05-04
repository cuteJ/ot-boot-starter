package com.onlythinking.starter.lettuce;

import com.lambdaworks.redis.RedisClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.DefaultLettucePool;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePool;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p> The describe </p>
 *
 * @author Li Xingping
 */
@Configuration
@ConditionalOnClass({LettuceConnection.class, RedisOperations.class, RedisClient.class})
@EnableConfigurationProperties(RedisProperties.class)
public class LettuceRedisAutoConfiguration {

    private final RedisProperties properties;

    @Autowired
    public LettuceRedisAutoConfiguration(RedisProperties properties) {
        this.properties = properties;
    }

    // 连接工厂
    @Bean
    @ConditionalOnMissingBean
    public RedisConnectionFactory lettuceConnectionFactory() {
        return applyProperties(createLettuceConnectionFactory());
    }

    // 连接池
    @Bean
    @ConditionalOnMissingBean
    public LettucePool lettucePool(RedisProperties properties) {
        if (getSentinelConfig() != null) {
            return new DefaultLettucePool(getSentinelConfig());
        }
        if (StringUtils.hasText(this.properties.getUrl())) {
            URI uri = getConnectionFromUri();
            DefaultLettucePool lettucePool = new DefaultLettucePool(uri.getHost(), uri.getPort(), poolConfig());
            lettucePool.setPassword(getPassword());
            return lettucePool;
        }
        DefaultLettucePool lettucePool = new DefaultLettucePool(properties.getHost(), properties.getPort(), poolConfig());
        lettucePool.setPassword(properties.getPassword());
        return lettucePool;
    }

    // 设置连接属性
    protected final LettuceConnectionFactory applyProperties(LettuceConnectionFactory factory) {
        configureConnection(factory);
        if (this.properties.isSsl()) {
            factory.setUseSsl(true);
        }
        factory.setDatabase(this.properties.getDatabase());
        if (this.properties.getTimeout() > 0) {
            factory.setTimeout(this.properties.getTimeout());
        }
        return factory;
    }

    // 配置连接
    private void configureConnection(LettuceConnectionFactory factory) {
        if (StringUtils.hasText(this.properties.getUrl())) {
            configureConnectionFromUrl(factory);
        } else {
            factory.setHostName(this.properties.getHost());
            factory.setPort(this.properties.getPort());
            if (this.properties.getPassword() != null) {
                factory.setPassword(this.properties.getPassword());
            }
        }
    }

    // 创建连接
    private LettuceConnectionFactory createLettuceConnectionFactory() {
        if (getSentinelConfig() != null) {
            return new LettuceConnectionFactory(lettucePool(properties));
        }
        if (getClusterConfiguration() != null) {
            return new LettuceConnectionFactory(getClusterConfiguration());
        }
        return new LettuceConnectionFactory(lettucePool(properties));
    }

    // 连接池配置
    private GenericObjectPoolConfig poolConfig() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        RedisProperties.Pool props = this.properties.getPool();
        if (null != props) {
            config.setMaxTotal(props.getMaxActive());
            config.setMaxIdle(props.getMaxIdle());
            config.setMinIdle(props.getMinIdle());
            config.setMaxWaitMillis(props.getMaxWait());
        }
        return config;
    }

    protected final RedisSentinelConfiguration getSentinelConfig() {
        if (this.properties.getSentinel() != null) {
            RedisProperties.Sentinel sentinelProperties = this.properties.getSentinel();
            if (sentinelProperties != null) {
                RedisSentinelConfiguration config = new RedisSentinelConfiguration();
                config.master(sentinelProperties.getMaster());
                config.setSentinels(createSentinels(sentinelProperties));
                return config;
            }
        }
        return null;
    }

    protected final RedisClusterConfiguration getClusterConfiguration() {
        if (this.properties.getCluster() != null) {
            RedisProperties.Cluster clusterProperties = this.properties.getCluster();
            RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());
            if (clusterProperties.getMaxRedirects() != null) {
                config.setMaxRedirects(clusterProperties.getMaxRedirects());
            }
            return config;
        }
        return null;
    }

    private List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList<RedisNode>();
        for (String node : StringUtils
          .commaDelimitedListToStringArray(sentinel.getNodes())) {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.valueOf(parts[1])));
            } catch (RuntimeException ex) {
                throw new IllegalStateException(
                  "Invalid redis sentinel " + "property '" + node + "'", ex);
            }
        }
        return nodes;
    }

    private void configureConnectionFromUrl(LettuceConnectionFactory factory) {
        String url = this.properties.getUrl();
        if (url.startsWith("rediss://")) {
            factory.setUseSsl(true);
        }
        URI uri = getConnectionFromUri();
        factory.setHostName(uri.getHost());
        factory.setPort(uri.getPort());
        if (uri.getUserInfo() != null) {
            String password = uri.getUserInfo();
            int index = password.indexOf(":");
            if (index >= 0) {
                password = password.substring(index + 1);
            }
            factory.setPassword(password);
        }
    }

    private String getPassword() {
        URI uri = getConnectionFromUri();
        if (uri.getUserInfo() != null) {
            String password = uri.getUserInfo();
            int index = password.indexOf(":");
            if (index >= 0) {
                password = password.substring(index + 1);
            }
            return password;
        }
        return null;
    }

    private URI getConnectionFromUri() {
        String url = this.properties.getUrl();
        try {
            return new URI(url);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Malformed 'spring.redis.url' " + url, ex);
        }
    }

    @Configuration
    protected static class RedisConfiguration {
        @Bean
        @ConditionalOnMissingBean(name = "redisTemplate")
        public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
          throws UnknownHostException {
            RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }

        @Bean
        @ConditionalOnMissingBean(StringRedisTemplate.class)
        public StringRedisTemplate stringRedisTemplate(
          RedisConnectionFactory redisConnectionFactory)
          throws UnknownHostException {
            StringRedisTemplate template = new StringRedisTemplate();
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }
    }

}
