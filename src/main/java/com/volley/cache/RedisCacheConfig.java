package com.volley.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@Slf4j
public class RedisCacheConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private Integer redisPort;

    @Value("${redis.timeout}")
    private Integer redisTimeout;
    @Value("${redis.maximumActiveConnectionCount}")
    private Integer redisMaximumActiveConnectionCount;

    @Bean
    public JedisPool jedisPool(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        log.info("Trying to connect to redis");
        jedisPoolConfig.setMaxTotal(redisMaximumActiveConnectionCount);
        return new JedisPool(jedisPoolConfig, redisHost, redisPort,redisTimeout);
    }

}
