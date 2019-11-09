package com.study.vdo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 从全局配置中获取值
 */
@Component
public class BookDO2 {
    @Value("${book.author}")
    private String author;
    @Value("${book.name}")
    private String name;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        //System.out.println("author=" +author + ",name=" + name);
        return "author=" + author + ",name=" + name;
    }
}
