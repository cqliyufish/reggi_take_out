package com.yu.reggie_take_out.sevice.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.reggie_take_out.entity.DishFlavor;
import com.yu.reggie_take_out.mapper.DishFlavorMapper;
import com.yu.reggie_take_out.sevice.DishFlavorService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
