package com.yu.reggie_take_out.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.reggie_take_out.common.R;
import com.yu.reggie_take_out.dto.DishDto;
import com.yu.reggie_take_out.entity.Category;
import com.yu.reggie_take_out.entity.Dish;
import com.yu.reggie_take_out.entity.DishFlavor;
import com.yu.reggie_take_out.sevice.CategoryService;
import com.yu.reggie_take_out.sevice.DishFlavorService;
import com.yu.reggie_take_out.sevice.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * 传入数据在Dish实体上多了flavor， 不能用Dish类接受，使用DishDTO(data transfer object) 用于
     * 展示层与服务层之间数据传输
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("add success");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //分页构造器对象
        // 用于查询
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        // 返回页面类型
        Page<DishDto> dishDtoPage = new Page<>();

        //添加构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);

        // 对象Page拷贝, records单独处理，里面存放的是菜品数据
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> lists = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            // 查询菜品分类名称
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(lists);
        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询dish和flavor信息
     * dish/1561244789366378497
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("update success");
    }

    /**
     * 根据条件查询菜品, 口味
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!= null, Dish::getCategoryId, dish.getCategoryId());
        //只查售卖中的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtos = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            // 查询菜品分类名称
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            // 查询菜品ID, 并查询相应口味
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper1);

            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        return  R.success(dishDtos);
    }

    /**
     * 修改售卖状态
     */

//    @PostMapping("/status/{newStatus}")
//    public R<String> updateStatus2(@PathVariable Long newStatus, Long ids){
//        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
//        updateWrapper.set("status", newStatus).in("id", ids);
//        dishService.update(updateWrapper);
//        return R.success("状态更新成功");
//    }

    /**
     * 批量起售停售
     * @param newStatus
     * @param ids
     * @return
     */
    @PostMapping("/status/{newStatus}")
    public R<String> updateStatus2(@PathVariable Long newStatus, @RequestParam List<Long> ids){
        log.info(ids.toString());
        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", newStatus).in("id", ids);
        dishService.update(updateWrapper);
        return R.success("状态更新成功");
    }


    /**
     * 批量起售删除
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info(ids.toString());
        dishService.removeWithFlavor(ids);
        return R.success("delete success");
    }






}
