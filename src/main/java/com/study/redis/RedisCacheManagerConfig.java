//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.study.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

/**
 * 这是一个配置类
 */
@Configuration
@ConditionalOnProperty(
        value = {"spring.cache.redis.ttl", "spring.cache.redis.time-to-live"},
        matchIfMissing = false
)
@ConditionalOnClass({RedisOperations.class, CacheManager.class, RedisConnectionFactory.class})
@AutoConfigureAfter({RedisAutoConfiguration.class})

public class RedisCacheManagerConfig {
    protected final Log log = LogFactory.getLog(this.getClass());
    public static final String EMPTY = "{0}";
    private String ttl = "3600";
    private Duration timeToLive;

    public RedisCacheManagerConfig() {
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * retemplate相关配置
     *
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer jacksonSeial = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.enableDefaultTyping(DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);

        // 值采用json序列化
        template.setValueSerializer(jacksonSeial);
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jacksonSeial);
        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        /*this.log.info( new Object[]{"begin init redis for ttl" + this.ttl});
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = (new ObjectMapper()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
        om.enableDefaultTyping(DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(SerializationPair.fromSerializer(jackson2JsonRedisSerializer)).entryTtl(this.timeToLive);
        String[] ttls = StringUtils.split(this.ttl, "|");
        Set<String> cacheNames = new HashSet();
        Map<String, RedisCacheConfiguration> configMap = new HashMap();
        String[] var8 = ttls;
        int var9 = ttls.length;

        for(int var10 = 0; var10 < var9; ++var10) {
            String s = var8[var10];
            String[] nameTime = s.split(",");
            this.log.info( "begin init redis " + nameTime[0] + " ttl" + nameTime[1]);
            cacheNames.add(nameTime[0]);
            configMap.put(nameTime[0], redisCacheConfiguration.entryTtl(Duration.ofSeconds(Long.valueOf(nameTime[1]))));
        }

        RedisCacheManager cacheManager = RedisCacheManager.builder(factory).initialCacheNames(cacheNames).withInitialCacheConfigurations(configMap).cacheDefaults(redisCacheConfiguration).build();
        return cacheManager;*/

        //全局redis缓存过期时间
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(1));
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisTemplate.getConnectionFactory());
        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
    }

    public String getTtl() {
        return this.ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public Duration getTimeToLive() {
        return this.timeToLive;
    }

    public void setTimeToLive(Duration timeToLive) {
        this.timeToLive = timeToLive;
    }
}
