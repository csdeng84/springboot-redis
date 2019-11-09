package com.study.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;

@Service
public class UserService {

    //@Autowired
    //private RestTemplate restTemplate;

    @Cacheable(value = "UserService")
    public String sayHello(String name) {
        String msg = System.currentTimeMillis() + "--->" + name + " get name from db======";
        System.out.println(msg);
        return msg;
    }

    @CachePut(value = "UserService")
    public String put(String name) {
        String msg = System.currentTimeMillis() + "--->" + name + " put to redis";
        System.out.println(msg);
        return msg;
    }

    public void threadDemo() throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newCachedThreadPool();

        Callable<String> c1 = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "finally";
            }
        };
        FutureTask<String> task = new FutureTask<String>(c1);
        threadPool.submit(task);
        String result = task.get();
        System.out.println(result);
    }
}
