package com.study.rabbitmq;

//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Service;

@Service
public class MyRabbitMqListener {

    /*@RabbitListener(queues = "fcsp.pd")
    public void mqListener(Object msg){
        System.out.println("收到消息:" + msg);
    }

    @RabbitListener(queues = "fcsp.pd")
    public void mqListener2(Message msg){
        System.out.println("收到消息:" + msg.getBody());
        System.out.println("收到消息:" + msg.getMessageProperties());
    }*/

}
