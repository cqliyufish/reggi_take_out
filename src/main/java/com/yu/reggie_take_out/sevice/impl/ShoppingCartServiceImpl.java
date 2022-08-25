package com.yu.reggie_take_out.sevice.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.reggie_take_out.entity.ShoppingCart;
import com.yu.reggie_take_out.mapper.ShoppingCartMapper;
import com.yu.reggie_take_out.sevice.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
