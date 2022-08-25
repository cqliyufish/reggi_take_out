package com.yu.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.reggie_take_out.common.R;
import com.yu.reggie_take_out.entity.Category;
import com.yu.reggie_take_out.entity.Employee;
import com.yu.reggie_take_out.sevice.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品分类
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("add category success");
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        //1. 构造分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //2. 构造条件构造器，按更新时间排序
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        //3.执行查询
        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 删除菜品
     */
    @DeleteMapping
    public R<String> delete(Long id){
        categoryService.remove(id);
        return R.success("category delete success");
    }

    /**
     * 根据ID修改信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("category update success");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        // 按类型和更新时间排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }
}
