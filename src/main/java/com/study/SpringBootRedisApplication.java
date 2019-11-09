package com.study;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EnableRabbit
//导入自己编写的配置文件
//@ImportResource(locations = {"classpath:beans.xml"})
@EnableCaching
@Configuration
public class SpringBootRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRedisApplication.class, args);
    }

    private String redisHost = "192.168.0.107";

    @Bean
    public Redisson redisson() {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisHost + ":6379").setDatabase(0);
        return (Redisson) Redisson.create(config);
    }
}
