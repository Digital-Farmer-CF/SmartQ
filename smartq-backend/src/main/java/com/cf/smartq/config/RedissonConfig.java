package com.cf.smartq.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@ConfigurationProperties(prefix = "spring.redis")
@Data
@Configuration
public class RedissonConfig {

    private String host;

    private Integer port;

    private Integer database;

    private String password;

    @PostConstruct
    public void init() {
        System.out.println("RedissonConfig: host=" + host + ", port=" + port + ", database=" + database + ", password=" + password);
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setDatabase(database)
                .setPassword(password);
        return Redisson.create(config);
    }
}
