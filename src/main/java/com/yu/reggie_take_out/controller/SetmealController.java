package com.yu.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.reggie_take_out.common.R;
import com.yu.reggie_take_out.dto.DishDto;
import com.yu.reggie_take_out.dto.SetmealDto;
import com.yu.reggie_take_out.entity.Category;
import com.yu.reggie_take_out.entity.Dish;
import com.yu.reggie_take_out.entity.Setmeal;
import com.yu.reggie_take_out.sevice.CategoryService;
import com.yu.reggie_take_out.sevice.SetmealDishService;
import com.yu.reggie_take_out.sevice.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 保存新建套餐
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("success");

    }

    /**
     * 分页查询
     * /page?page=1&pageSize=20
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        // 对象Page拷贝, records单独处理，里面存放的是菜品数据
        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> lists = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(lists);

        return R.success(setmealDtoPage);
    }

    /**
     * 套餐删除
     * /setmeal?ids=1561594422072532993,1561589100985499650
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info(ids.toString());
        setmealService.removeWithDish(ids);
        return R.success("delete success");
    }

    /**
     *修改套餐状态
     */
    @PostMapping("/status/{newStatus}")
    public R<String> updateStatus2(@PathVariable Long newStatus, @RequestParam List<Long> ids){
        UpdateWrapper<Setmeal> queryWrapper = new UpdateWrapper<>();
        queryWrapper.set("status", newStatus).in("id", ids);
        setmealService.update(queryWrapper);
        return R.success("状态更新成功");
    }

//    @PostMapping("/status/{newStatus}")
//    public R<String> updateStatus2(@PathVariable Long newStatus, Long ids){
//        UpdateWrapper<Setmeal> updateWrapper = new UpdateWrapper<>();
//        updateWrapper.set("status", newStatus).in("id", ids);
//        setmealService.update(updateWrapper);
//        return R.success("状态更新成功");
//    }


    /**
     * 根据套餐id查询套餐，菜品
     * http://localhost:8080/dish/1561244789366378497
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);

        return R.success(setmealDto);
    }

    /**
     * 修改套餐id查询套餐，菜品
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("update success");
    }

    /**
     * 根据条件查询套餐
     * /list?categoryId=1413342269393674242&status=1
     * 传入数据为键值对， 直接接收
     * 可以用更大的实体接受id和status
     */

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);

    }
}
