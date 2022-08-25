package com.yu.reggie_take_out.common;

/**
 *基于ThreadLocal封装工具类，用于保存和获取用户id
 */
public class BaseContext {
    //1. 创建ThreadLocal类，泛型Long（id为Long）
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
