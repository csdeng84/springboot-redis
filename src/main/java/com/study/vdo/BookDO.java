package com.study.vdo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;

@Component
@ConfigurationProperties(prefix = "book")
public class BookDO {

    //JRS303数据校验
    //@Email
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
        return "method 1: author=" + author + ",name=" + name;
    }
}
