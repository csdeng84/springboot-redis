package com.study.redis;

/**
 * 获取库存回调
 *
 * @author yuhao.wang
 */
public interface IStockCallback {
    /**
     * 获取库存
     *
     * @return
     */
    int getStock();
}
