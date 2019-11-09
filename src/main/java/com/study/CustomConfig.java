package com.study;

import com.study.service.BookService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//指明当前类是一个配置类
//以前是在<bean>写配置类文件
@Configuration
public class CustomConfig {

    //@Bean将方法的返回值添加到容器中，容器中默认的id就是方法名
    @Bean
    public BookService bookService() {
        return new BookService();
    }
}
