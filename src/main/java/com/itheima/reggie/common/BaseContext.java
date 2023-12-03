package com.itheima.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前登录用户id
 */
public class BaseContext {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return long
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
