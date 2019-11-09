package com.study.controller;

import com.study.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserCongroller {

    @Autowired
    private UserService userService;

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(@RequestParam("name") String name) {
        return userService.sayHello(name);
    }

    @RequestMapping("/put")
    @ResponseBody
    public String put(@RequestParam("name") String name) {
        return userService.put(name);
    }

}
