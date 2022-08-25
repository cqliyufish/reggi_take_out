package com.yu.reggie_take_out.sevice.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.reggie_take_out.common.BaseContext;
import com.yu.reggie_take_out.common.CustomException;
import com.yu.reggie_take_out.entity.*;
import com.yu.reggie_take_out.mapper.OrdersMapper;
import com.yu.reggie_take_out.sevice.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;
    /**
     * 用户下单
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //1. 获得当前userId
        Long currentId = BaseContext.getCurrentId();
        //2. 用userID查询购物车
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

        if(shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("购物车为空，下单失败");
        }

        //3. 用userID查询用户数据，地址数据
        User user = userService.getById(currentId);

        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new CustomException("地址信息错误，下单失败");
        }

        //4. 向order 和 order_detail插入数据
        long orderId = IdWorker.getId(); // 随机生成订单号
        AtomicInteger amount = new AtomicInteger(0); // 原子操作，线程安全
        List<OrderDetail> orderDetailList = shoppingCarts.stream().map(item ->{
            OrderDetail orderDetail = new OrderDetail();
            // orderDetail参数设置
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());// 菜品数量
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());//菜品ID
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;


        }).collect(Collectors.toList());

        //4.1 设定order其他属性
        orders.setNumber(String.valueOf(orderId));
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2); //待派送
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentId);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null?"":addressBook.getProvinceName())
                +(addressBook.getCityName() == null?"":addressBook.getCityName())
                +(addressBook.getDistrictName() == null?"":addressBook.getDistrictName())
                +(addressBook.getDetail() == null?"":addressBook.getDetail()));
        //存入order表中
        this.save(orders);
        //存入orderDetail 多条数据
        orderDetailService.saveBatch(orderDetailList);
        //5. 清空购物车
        shoppingCartService.remove(queryWrapper);

    }
}
