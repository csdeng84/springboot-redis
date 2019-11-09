package com.study.controller;

import com.study.service.StockService4Lua;
import com.study.service.StockService4RedisTemplate;
import com.study.service.StockService4Redisson;
import com.study.vdo.BookDO;
import com.study.vdo.BookDO2;
import com.study.vdo.BookDO3;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

@RestController
public class StockController {

    @Autowired
    private StockService4Lua stockService4Lua;

    @Autowired
    private StockService4RedisTemplate stockService4RedisTemplate;

    @Autowired
    private StockService4Redisson stockService4Redisson;


    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String STOCK_KEY = "dcs_redis_key:stock:";

    private static int COUNT = 1000;
    //ExecutorService executorService = Executors.newFixedThreadPool(100);
    private static CountDownLatch cdl = new CountDownLatch(COUNT);

    //测试线程
    public class MyRunable<Object> implements Runnable {
        @Override
        public void run() {
            try {
                cdl.await();
                //String result = "" + stockService4Redisson.stock(STOCK_KEY + 1L, 1L, 1);
                String result = "" + stockService4RedisTemplate.mdfStock(STOCK_KEY + 1L, 1L, 1);
                System.out.println(System.currentTimeMillis() + ",end,result=" + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //并发测试
    @RequestMapping(value = "/batch", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String startTest() throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();
        System.out.println(Thread.currentThread() + " ---------main start--------" + startTime);
        for (int i = 0; i < COUNT; i++) {
            new Thread(new MyRunable<>()).start();
            cdl.countDown();
            System.out.println(System.currentTimeMillis() + "准备执行===" + i);

        }
        String ret = " ---------main end cost time=" + (System.currentTimeMillis()  - startTime);
        System.out.println(ret + "last vale is " + stockService4RedisTemplate.getStock(STOCK_KEY + 1L));
        return ret;
     }


    //测试
    @RequestMapping(value = "/testRessionTime", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String testRessionTime() throws ExecutionException, InterruptedException {
        Long startTime = System.currentTimeMillis();
        System.out.println(Thread.currentThread() + " ---------start--------" + startTime);
        for (int i = 0; i < COUNT; i++) {
            stockService4Redisson.stock(STOCK_KEY + 1L, 1L, 1);
        }
        String result = "---------end--------" + COUNT + "times cost " + (System.currentTimeMillis() - startTime);
        System.out.println( result);
        return result;
      }

    //测试
    @RequestMapping(value = "/testRedisTemplate", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String testRedisTemplate() throws ExecutionException, InterruptedException {
        Long startTime = System.currentTimeMillis();
        System.out.println(Thread.currentThread() + " ---------start--------" + startTime);
        for (int i = 0; i < COUNT; i++) {
            double res = stockService4RedisTemplate.mdfStock(STOCK_KEY + 1L,100,0.1);
            System.out.println(" ---------result--------" + res);
        }
        String result = "---------end--------" + COUNT + "times cost " + (System.currentTimeMillis() - startTime);
        System.out.println( result);
        return result;
    }

    //测试
    @RequestMapping(value = "/testLua", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String testLua() throws ExecutionException, InterruptedException {
        Long startTime = System.currentTimeMillis();
        System.out.println(Thread.currentThread() + " ---------start--------" + startTime);
        for (int i = 0; i < COUNT; i++) {
            stockbylua(1L,1);
        }
        String result = "---------end--------" + COUNT + "times cost " + (System.currentTimeMillis() - startTime);
        System.out.println( result);
        return result;
    }

    @RequestMapping(value = "/stockbylua", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String stockbylua(@RequestParam long commodityId, int num) {
        System.out.println(System.currentTimeMillis() + "------doStock-----");
        // 商品ID commodityId
        //num 扣减数
        String redisKey = STOCK_KEY + commodityId;
        long stock = stockService4Lua.mdfStockByLua(redisKey, 60 * 60, num, () -> stockInit(commodityId));
        if (stock >= 0) {
            return "扣减后还剩下!:" + stock;
        } else if (stock == -1) {
            return "库存不足!!!!";
        } else {
            return "加锁失败";
        }
    }

    @RequestMapping(value = "/stockbyredisson", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String stockbyredisson(@RequestParam long commodityId, int num) {
        System.out.println(System.currentTimeMillis() + "------doStock-----");
        // 商品ID commodityId
        //num 扣减数
        String redisKey = STOCK_KEY + commodityId;
        long stock = stockService4Redisson.stock(redisKey, commodityId, num);
        if (stock >= 0) {
            return "扣减后还剩下!:" + stock;
        } else if (stock == -1) {
            return "库存不足!!!!";
        } else {
            return "加锁失败";
        }
    }


    /**
     * 获取初始的库存到缓存
     *
     * @return
     */
    @RequestMapping(value = "/initStock", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String initStock(@RequestParam long commodityId) {
        String redisKey = STOCK_KEY + commodityId;
        Long initNum = stockService4Lua.initStock(redisKey, 60 * 60, () -> stockInit(commodityId));
        return "初始化库存为:" + initNum.toString();
    }

    public int stockInit(long commodityId) {
        return 500000;
    }

    @RequestMapping(value = "/getStock", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getStock(@RequestParam long commodityId) {
        // 商品ID
        //long commodityId = 1;
        String redisKey = STOCK_KEY + commodityId;
        return "当前库存为:" + stockService4Lua.getStock(redisKey);
    }

    @RequestMapping(value = "/addStock", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String addStock(@RequestParam long commodityId, int num) {
        // 商品ID long commodityId
        // 增加的库存 num
        String redisKey = STOCK_KEY + commodityId;
        return "增加库存后当前总量为:" + String.valueOf(stockService4Lua.addStock(redisKey, num));
    }

    //资源注入
    @Autowired
    private BookDO bookDO;

    @RequestMapping(value = "/resource1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String resource1() {
        System.out.println(bookDO.toString());
        return bookDO.toString();
    }

    //资源注入
    @Autowired
    private BookDO2 bookDO2;

    @RequestMapping(value = "/resource2", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String resource2() {
        System.out.println(bookDO2.toString());
        return bookDO2.toString();
    }

    //资源注入
    @Autowired
    private BookDO3 bookDO3;

    @RequestMapping(value = "/resource3", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String resource3() {
        System.out.println(bookDO3.toString());
        return bookDO3.toString();
    }

//    @Autowired
//    private MyRabbitMqManage myRabbitMqManage;
//    @RequestMapping(value = "/initmq", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public void initmq() {
//        myRabbitMqManage.initExchange();
//    }

//    @RequestMapping(value = "/sendmq", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public String testMq(@RequestParam String exchange, String routekey,String msg) {
//        try{
//            Map<String,Object> msgMap = new HashMap<String,Object>();
//            msgMap.put("MSG",msg);
//
//            msgMap.put("BOOK",new BigDecimal("19999.0"));
//            rabbitTemplate.convertAndSend(exchange,routekey,msgMap);
//            return "消息发送完成 ！！" ;
//        }
//        catch (Exception ex){
//            ex.printStackTrace();
//            return ex.getLocalizedMessage();
//        }
//    }
//
//    @RequestMapping(value = "/getmq", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public Object getMq(@RequestParam String queuename) {
//        try{
//            return  rabbitTemplate.receiveAndConvert(queuename);
//        }
//        catch (Exception ex){
//            return ex.getLocalizedMessage();
//        }
//    }


}
