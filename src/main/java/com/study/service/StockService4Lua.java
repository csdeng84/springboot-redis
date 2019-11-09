package com.study.service;

import com.study.redis.IStockCallback;
import com.study.redis.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 扣库存
 *
 * @author yuhao.wang
 */
@Service
public class StockService4Lua {
    Logger logger = LoggerFactory.getLogger(StockService4Lua.class);

    /**
     * -3:库存未初始化
     */
    public static final long UNINITIALIZED_STOCK = -3L;

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
     * @param stockCallback 初始化库存回调函数
     * @return -2:库存不足; -1:不限库存; 大于等于0:扣减库存之后的剩余库存
     */
    public long mdfStockByLua(String key, long expire, int num, IStockCallback stockCallback) {
        long stock = mdfStockByLua(key, num);
        return stock;
    }

    /**
     * 加库存(还原库存)
     *
     * @param key 库存key
     * @param num 库存数量
     * @return
     */
    public long addStock(String key, int num) {
        return addStock(key, null, num);
    }

    /**
     * 加库存
     *
     * @param key    库存key
     * @param expire 过期时间（秒）
     * @param num    库存数量
     * @return
     */
    public long addStock(String key, Long expire, int num) {
        boolean hasKey = redisTemplate.hasKey(key);
        // 判断key是否存在，存在就直接更新
        if (hasKey) {
            return redisTemplate.opsForValue().increment(key, num);
        }

        Assert.notNull(expire, "初始化库存失败，库存过期时间不能为null");
        RedisLock redisLock = new RedisLock(redisTemplate, key);
        try {
            if (redisLock.lock()) {
                // 获取到锁后再次判断一下是否有key
                hasKey = redisTemplate.hasKey(key);
                if (!hasKey) {
                    //库存处理
                    redisTemplate.opsForValue().set(key, num, expire, TimeUnit.SECONDS);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            redisLock.unlock();
        }

        return num;
    }

    /**
     * 获取库存
     *
     * @param key 库存key
     * @return -1:不限库存; 大于等于0:剩余库存
     */
    public String getStock(String key) {
        String stock = (String) redisTemplate.opsForValue().get(key);
        return stock;
    }
    
    
    /**
     * 扣库存
     *
     * @param key 库存key
     * @param num 扣减库存数量
     * @return 扣减之后剩余的库存【-3:库存未初始化; -2:库存不足; -1:不限库存; 大于等于0:扣减库存之后的剩余库存】
     */
    private Long mdfStockByLua(String key, int num) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("test.lua"));
        redisScript.setResultType(Long.class);
        // 脚本里的KEYS参数
        List<String> keys = new ArrayList<>();
        keys.add(key);
        // 脚本里的ARGV参数
        Object o = redisTemplate.execute(redisScript, keys, num);
        return (Long) o;
    }

}
