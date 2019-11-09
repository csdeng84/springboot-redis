package com.study.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

//@Service
//@Component
//@PropertySource(value = "classpath:mymqadmin.properties")
//@ConfigurationProperties(prefix = "mymqadmin")
public class MyRabbitMqManage {
  /*  @Autowired
    private AmqpAdmin amqpAdmin;
    private String exchange;
    private String queuename;
    private String routekey;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getQueuename() {
        return queuename;
    }

    public void setQueuename(String queuename) {
        this.queuename = queuename;
    }

    public String getRoutekey() {
        return routekey;
    }

    public void setRoutekey(String routekey) {
        this.routekey = routekey;
    }

    public void initExchange(){
        //String exchange = "fcsp.fanout";
        //String queuename = "fcsp.pd";
        //String routekey = "fcsp.pd";
        amqpAdmin.declareExchange(new FanoutExchange(exchange));
        amqpAdmin.declareQueue(new Queue(queuename));
        amqpAdmin.declareBinding(new Binding(queuename, Binding.DestinationType.QUEUE,
               exchange, routekey,null));

    }
    */

}
