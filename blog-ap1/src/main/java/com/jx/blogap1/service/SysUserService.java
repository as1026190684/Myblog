package com.jx.blogap1.service;

import com.jx.blogap1.dao.pojo.SysUser;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.vo.LoginUserVo;
import com.jx.blogap1.vo.UserVo;
import com.jx.blogap1.vo.params.FilePath;

public interface SysUserService {
    SysUser findUserById(Long id);

    //登陆查找用户
    SysUser findUser(String account, String pwd);

    // 根据token查找用户信息
    Result getUserInfoByToken(String token);

    SysUser getUserInfoByToken1(String token);

    //注册（通过account查找是否有这个用户）
    SysUser findUserByAccount(String account);

    //注册 存入用户信息
    void save(SysUser sysUser);

    //评论（通过评论者id和给谁评论的toUid来查找对应用户的id，头像，名称等）
    UserVo findUserVoById(Long id);

    //更新用户头像
    Result updateUserAvatarByToken(FilePath filePath, String token);
}
