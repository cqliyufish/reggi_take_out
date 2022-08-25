package com.yu.reggie_take_out.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yu.reggie_take_out.dto.SetmealDto;
import com.yu.reggie_take_out.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，保存套餐和菜品关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);

    public SetmealDto getByIdWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);

}
