package com.jx.blogap1.service;

import com.jx.blogap1.dao.pojo.SysUser;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.vo.params.LoginParam;

public interface LoginService {

    //登陆
    Result login(LoginParam loginParam);

    SysUser checkToken(String token);

    //退出
    Result logout(String token);

    //注册
    Result register(LoginParam loginParam);


}
