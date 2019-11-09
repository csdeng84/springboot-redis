package com.study.service;

import com.study.redis.IStockCallback;
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
public class StockService4RedisTemplate {
    Logger logger = LoggerFactory.getLogger(StockService4RedisTemplate.class);

    /**
     * Redis 客户端
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 初始化
     *
     * @param key           库存key
     * @param expire        库存有效时间,单位秒
     * @param stockCallback 初始化库存回调函数
     * @return -2:库存不足; -1:不限库存; 大于等于0:扣减库存之后的剩余库存
     */
    public long initStock(String key, long expire, IStockCallback stockCallback) {
        // 获取初始化库存
        final int initStock = stockCallback.getStock();
        try {
            // 将库存设置到redis
            redisTemplate.opsForValue().set(key, initStock, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return initStock;
    }


    /**
     * @param key           库存key
     * @param expire        库存有效时间,单位秒
     * @param num           扣减数量
     * @return -2:库存不足; -1:不限库存; 大于等于0:扣减库存之后的剩余库存
     */
    public double mdfStock(String key, long expire, double num) {
        boolean hasKey = redisTemplate.hasKey(key);
        // 判断key是否存在，存在就直接更新
        if (hasKey) {
            return redisTemplate.opsForValue().increment(key, num);
        }
        else
            return -1.0;
    }


        /**
         * 获取库存
         *
         * @param key 库存key
         * @return -1:不限库存; 大于等于0:剩余库存
         */
        public String getStock (String key){
            Object stock = redisTemplate.opsForValue().get(key);
            return stock.toString();
        }

}
