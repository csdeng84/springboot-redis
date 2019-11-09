package com.study.service;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 扣库存
 *
 * @author yuhao.wang
 */
@Service
public class StockService4Redisson {
    Logger logger = LoggerFactory.getLogger(StockService4Redisson.class);

    @Autowired
    private Redisson redisson;

    private final String lockKey = "redis_lock_";

    /**
     * Redis 客户端
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    public int stock(String key, Long stockId, int num) {

        String LockKey = lockKey + stockId;
        RLock lock = redisson.getLock(LockKey);
//        String cliendId = UUID.randomUUID().toString();
////        Boolean result = redisTemplate.opsForValue().setIfAbsent(LockKey,cliendId,10,TimeUnit.SECONDS);
////
////        if(false == result){
////            System.out.println("加锁失败");
////            return -999;
////        }
        try {
            lock.tryLock(30, TimeUnit.SECONDS);
            Object obj = redisTemplate.opsForValue().get(key);
            Long stock = Long.parseLong(obj.toString());
            if (stock > num) {
                Long realStock = stock - num;
                redisTemplate.opsForValue().set(key, realStock + "", 10 * 60 * 60, TimeUnit.SECONDS);
                System.out.println("库存剩余：" + realStock);
                return realStock.intValue();
            } else {
                System.out.println("库存不足");
                return -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            if(cliendId.equals(redisTemplate.opsForValue().get(LockKey))){
////                redisTemplate.delete(LockKey);
////            }
            lock.unlock();
        }
        return 0;
    }

}
