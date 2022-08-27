package com.yu.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.reggie_take_out.common.BaseContext;
import com.yu.reggie_take_out.common.R;
import com.yu.reggie_take_out.dto.DishDto;
import com.yu.reggie_take_out.entity.Dish;
import com.yu.reggie_take_out.entity.Orders;
import com.yu.reggie_take_out.entity.Setmeal;
import com.yu.reggie_take_out.sevice.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("下单数据 ： {}", orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }
    /**
     * 查询当前用户订单
     */

    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        ordersService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }
    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, Long number){

        System.out.println(number);
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getOrderTime);
        queryWrapper.eq(number!=null, Orders::getId, number);
        ordersService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);

    }

    @PutMapping
    public R<String> update(@RequestBody Orders order){
        log.info(order.toString());
        Long orderId = order.getId();
        Integer status = order.getStatus();
        System.out.println(status);
        if(status == 3){
            UpdateWrapper<Orders> queryWrapper = new UpdateWrapper<>();
            queryWrapper.set("status", status + 1).eq("id", orderId);
            ordersService.update(queryWrapper);
            return R.success("修改成功");
        }
        return R.error("不能修改");

    }
}
