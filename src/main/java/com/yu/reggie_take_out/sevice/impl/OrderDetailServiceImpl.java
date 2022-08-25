package com.yu.reggie_take_out.sevice.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.reggie_take_out.entity.OrderDetail;
import com.yu.reggie_take_out.mapper.OrderDetailMapper;
import com.yu.reggie_take_out.sevice.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
