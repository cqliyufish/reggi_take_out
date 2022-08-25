package com.yu.reggie_take_out.sevice.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.reggie_take_out.common.CustomException;
import com.yu.reggie_take_out.dto.SetmealDto;
import com.yu.reggie_take_out.entity.Dish;
import com.yu.reggie_take_out.entity.Setmeal;
import com.yu.reggie_take_out.entity.SetmealDish;
import com.yu.reggie_take_out.mapper.SetmealMapper;
import com.yu.reggie_take_out.sevice.SetmealDishService;
import com.yu.reggie_take_out.sevice.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，保存套餐和菜品关联关系
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //1. 保存套餐基本信息
        this.save(setmealDto);
        //2. 保存菜品套餐关联信息，操作setmeal_dish表格
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        dishes.stream().map((item) -> {

            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(dishes);

    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //1.查询套餐状态是否可以删除
        // select count(*) from setmeal where id in ids and status = 1
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);
        if(count > 0){
            // 不能删除
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        // 2. 删除setmeal
        this.removeByIds(ids);
        //3. 删除setmeal_dish
        //delete from setmeal_dish where setmeal id in ids
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);

        setmealDishService.remove(setmealDishLambdaQueryWrapper);

    }

    /**
     * 根据套餐ID查询套餐和菜品信息
     * @param id
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //1. 查询套餐基本信息
        Setmeal setmeal = this.getById(id);
        //2. 查询包含菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> lists = setmealDishService.list(queryWrapper);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        setmealDto.setSetmealDishes(lists);
        return setmealDto;

    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新套餐基本信息
        this.updateById(setmealDto);
        //删除SetmealDish
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //重新插入SetmealDish
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        dishes.stream().map((item) -> {

            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(dishes);






    }
}
