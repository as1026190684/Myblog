package com.jx.blogap1.utils;

import com.jx.blogap1.dao.pojo.SysUser;

/**
 * 线程本地变量（每个变量只能在本线程使用）
 * @author YYTE_JX
 * @date 2021/10/4 0004
 * @param
 * @return
 */
public class UserThreadLocal {

    private UserThreadLocal() {
    }

    private static final ThreadLocal<SysUser> LOCAL = new ThreadLocal<>();

    /**
     * 放入
     * @author YYTE_JX
     * @date 2021/10/4 0004
     * @param sysUser
     * @return void
     */
    public static void put(SysUser sysUser) {
        LOCAL.set(sysUser);
    }

    /**
     *  获取
     * @author YYTE_JX
     * @date 2021/10/4 0004
     * @param
     * @return com.jx.blogap1.dao.pojo.SysUser
     */
    public static SysUser get() {
        return LOCAL.get();
    }

    /**
     * 删除
     * @author YYTE_JX
     * @date 2021/10/4 0004
     * @param
     * @return void
     */
    public static void remove() {
        LOCAL.remove();
    }
}
