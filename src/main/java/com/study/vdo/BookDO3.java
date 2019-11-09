package com.study.vdo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:book.properties")
@ConfigurationProperties(prefix = "mybook")
public class BookDO3 {

    private String author;
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
        return "method 3: author=" + author + ",name=" + name;
    }
}
