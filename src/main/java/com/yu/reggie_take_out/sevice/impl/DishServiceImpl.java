package com.yu.reggie_take_out.sevice.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.reggie_take_out.common.CustomException;
import com.yu.reggie_take_out.dto.DishDto;
import com.yu.reggie_take_out.entity.Dish;
import com.yu.reggie_take_out.entity.DishFlavor;
import com.yu.reggie_take_out.mapper.DishMapper;
import com.yu.reggie_take_out.sevice.DishFlavorService;
import com.yu.reggie_take_out.sevice.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 1. DishDto 继承Dish， 保存菜品基本信息
        this.save(dishDto);
        // 2. 保存口味数据,注意导入Dish ID 到flavor中
        Long dishId = dishDto.getId();
        log.info(dishId.toString());
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{
           item.setDishId(dishId);
           return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //1.查询dish基本信息
        Dish dish = this.getById(id);

        //2. 查询Flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        //3.对象拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //1.更新Dish表
        this.updateById(dishDto);

        //2. 更新DishFlavor表，先删除原flavor, 再插入
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //插入新数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**批量删除套餐
     */
    @Override
    public void removeWithFlavor(List<Long> ids) {
        // dish 表中删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);
        int count = this.count(queryWrapper);
        if(count > 0){
            throw new CustomException("菜品正在售卖中，不能删除");
        }
        this.removeByIds(ids);
        //dishFlavor表中更近dishID删除
        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper1);

    }
}
