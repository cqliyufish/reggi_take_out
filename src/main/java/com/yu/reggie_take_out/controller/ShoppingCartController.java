package com.yu.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yu.reggie_take_out.common.BaseContext;
import com.yu.reggie_take_out.common.R;
import com.yu.reggie_take_out.entity.ShoppingCart;
import com.yu.reggie_take_out.sevice.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * shoppingCart/add JSON格式传入数据
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info(shoppingCart.toString());
        // 1.设置用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        // 2. 查询新加菜品是否在购物车，如果是，修改数量
        // 2.1 判断是菜品 or 套餐
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        if(dishId != null){
            // 添加为菜品, 根据userId 和 dishId联合查询
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        }else{
            // 添加为套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        // 2.2 判断是否已存在
        if(one != null){
            // 更新数量
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            shoppingCartService.updateById(one);
        }else{
            // 不存在，第一次入库，数量设为1, 设置入库时间
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }
        return R.success(one);
    }

    /**
     * 查看购物车
     * /shoppingCart/list'
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        // 根据userId查询
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * shoppingCart/clean
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper= new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("删除成功");
    }
}
