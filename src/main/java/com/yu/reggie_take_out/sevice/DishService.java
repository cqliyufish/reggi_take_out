package com.yu.reggie_take_out.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yu.reggie_take_out.dto.DishDto;
import com.yu.reggie_take_out.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品， 插入口味。操作2张表: dish dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

    void removeWithFlavor(List<Long> ids);
}
