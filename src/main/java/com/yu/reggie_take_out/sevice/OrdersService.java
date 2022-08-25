package com.yu.reggie_take_out.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.yu.reggie_take_out.entity.Orders;

public interface OrdersService extends IService<Orders> {
    /**
     * 用户下单
     */
    public void submit(Orders orders);
}
