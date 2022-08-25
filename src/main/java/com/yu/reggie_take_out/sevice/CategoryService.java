package com.yu.reggie_take_out.sevice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yu.reggie_take_out.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
