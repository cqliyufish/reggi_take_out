package com.yu.reggie_take_out.sevice.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.reggie_take_out.common.CustomException;
import com.yu.reggie_take_out.entity.Category;
import com.yu.reggie_take_out.entity.Dish;
import com.yu.reggie_take_out.entity.Setmeal;
import com.yu.reggie_take_out.mapper.CategoryMapper;
import com.yu.reggie_take_out.sevice.CategoryService;
import com.yu.reggie_take_out.sevice.DishService;
import com.yu.reggie_take_out.sevice.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    /**
     * 根据ID删除分类
     */
    public void remove(Long id) {
        //1. 查询当前分类是否关联菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if (count1 > 0){
            // 抛出业务异常
            throw new CustomException("Exist dish related to category, delete failed");
        }
        //2. 查询当前分类是否关联套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2> 0){
            // 抛出业务异常
            throw new CustomException("Exist combo related to category, delete failed");
        }
        //正常删除
        super.removeById(id);
    }
}
