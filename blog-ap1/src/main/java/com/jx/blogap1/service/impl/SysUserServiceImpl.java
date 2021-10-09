package com.jx.blogap1.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jx.blogap1.dao.mapper.SysUserMapper;
import com.jx.blogap1.dao.pojo.SysUser;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.result.ResultCodeEnum;
import com.jx.blogap1.service.SysUserService;
import com.jx.blogap1.utils.JWTUtils;
import com.jx.blogap1.vo.LoginUserVo;
import com.jx.blogap1.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public SysUser findUserById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser == null) {
            sysUser = new SysUser();
            sysUser.setNickname("YTTE");
        }
        return sysUser;
    }

    /**
     * 登陆查找用户
     * @author YYTE_JX
     * @date 2021/10/3 0003
     * @param account
     * @param pwd
     * @return com.jx.blogap1.dao.pojo.SysUser
     */
    @Override
    public SysUser findUser(String account, String pwd) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account);
        queryWrapper.eq(SysUser::getPassword, pwd);
        queryWrapper.select(SysUser::getId, SysUser::getAccount, SysUser::getAvatar, SysUser::getNickname);
        queryWrapper.last("limit 1");
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);

        return sysUser;
    }

    /**
     * 根据token查找用户信息
     * @author YYTE_JX
     * @date 2021/10/3 0003
     * @param token
     * @return com.jx.blogap1.result.Result
     */
    @Override
    public Result getUserInfoByToken(String token) {
        Map<String, Object> map = JWTUtils.checkToken(token);
        if (map == null){
            return Result.build(ResultCodeEnum.LOGIN_AUTH.getCode(),ResultCodeEnum.LOGIN_AUTH.getMessage());
        }

        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if (StringUtils.isBlank(userJson)){
            return Result.build(ResultCodeEnum.LOGIN_AUTH.getCode(),ResultCodeEnum.LOGIN_AUTH.getMessage());
        }
        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);

        //TODO 测试beanutils是否成功
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setAccount(sysUser.getAccount());
        loginUserVo.setAvatar(sysUser.getAvatar());
        loginUserVo.setId(sysUser.getId());
        loginUserVo.setNickname(sysUser.getNickname());

        return Result.success(loginUserVo);
    }
    @Override
    public SysUser getUserInfoByToken1(String token) {
        Map<String, Object> map = JWTUtils.checkToken(token);
        if (map == null){
            return null;
        }

        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if (StringUtils.isBlank(userJson)){
            return null;
        }
        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);

        //TODO 测试beanutils是否成功
        return sysUser;
    }


    /**
     * 注册（通过account查找是否有这个用户）
     * @author YYTE_JX
     * @date 2021/10/4 0004
     * @param account
     * @return com.jx.blogap1.dao.pojo.SysUser
     */
    @Override
    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account);
        queryWrapper.last("limit 1");

        return sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 注册 存入用户信息
     * @author YYTE_JX
     * @date 2021/10/4 0004
     * @param sysUser
     * @return void
     */
    @Override
    public void save(SysUser sysUser) {
        //mybatis -plus 默认生成的id是分布式id，采用的雪花算法，不会出现相同的情况
        this.sysUserMapper.insert(sysUser);

    }

    /**
     * 评论（通过评论者id和给谁评论的toUid来查找对应用户的id，头像，名称等）
     * @author YYTE_JX
     * @date 2021/10/5 0005
     * @param id
     * @return com.jx.blogap1.vo.UserVo
     */
    @Override
    public UserVo findUserVoById(Long id) {

        SysUser sysUser = sysUserMapper.selectById(id);
        //判断是否非空，给默认值
        if (sysUser == null){
            sysUser = new SysUser();
            sysUser.setId(1L);
            sysUser.setAvatar("/static/img/logo.b3a48c0.png");
            sysUser.setNickname("YTTE-游客");
        }

        //非空时
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(sysUser, userVo);

        return userVo;
    }
}
