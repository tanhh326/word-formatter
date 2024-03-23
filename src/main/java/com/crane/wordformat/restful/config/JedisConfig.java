package com.crane.wordformat.restful.config;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Slf4j
@Configuration
public class JedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private Integer redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Value("${spring.data.redis.database}")
    private Integer database;

    @Value("${spring.data.redis.jedis.pool.max-idle}")
    private Integer maxIdle;

    @Value("${spring.data.redis.jedis.pool.min-idle}")
    private Integer minIde;

    @Value("${spring.data.redis.jedis.pool.max-active}")
    private Integer maxActive;


    @Bean
    public JedisPool jedisPool(){
        // 初始化
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIde);
        jedisPoolConfig.setMaxTotal(maxActive);
        jedisPoolConfig.setTestOnBorrow(false);
        jedisPoolConfig.setTestOnReturn(true);
        log.info("redis properties {}, jedisPoolConfig={}" ,  this.toString(), JSONObject.toJSONString(jedisPoolConfig));
        return new JedisPool(jedisPoolConfig, redisHost, redisPort,0, redisPassword, database);
    }

    @Override
    public String toString() {
        return "JedisConfig{" +
                "redisHost='" + redisHost + '\'' +
                ", redisPort=" + redisPort +
                ", redisPassword='" + redisPassword + '\'' +
                ", database=" + database +
                '}';
    }
}

