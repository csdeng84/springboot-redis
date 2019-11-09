//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.study.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    protected final Log log = LogFactory.getLog(this.getClass());
    public static final String EMPTY = "{0}";
    @Autowired
    private RedisTemplate redisTemplate;

    public RedisConfig() {
    }

    @Bean
    public RedisTemplate redisTemplateInit() {
        this.log.info(new Object[]{"begin init RedisTemplate......"});
        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return this.redisTemplate;
    }
}
