package com.jx.blogap1.service.impl;

import com.alibaba.fastjson.JSON;
import com.jx.blogap1.dao.pojo.SysUser;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.result.ResultCodeEnum;
import com.jx.blogap1.service.LoginService;
import com.jx.blogap1.service.SysUserService;
import com.jx.blogap1.utils.JWTUtils;
import com.jx.blogap1.vo.params.LoginParam;
import io.netty.util.internal.StringUtil;
//import net.bytebuddy.implementation.bytecode.assign.primitive.PrimitiveWideningDelegate;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {


    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String slat = "YTTEjx!#@";

    @Value("${register.InviteCode}")
    private String registerInviteCode ;
    /**
     * 登陆
     *
     * @param loginParam
     * @return com.jx.blogap1.result.Result
     * <p>
     * 1、检查参数是否合法
     * 2、根据用户名和密码去user表中查询 是否存在
     * 3、如果不存在→→→→→登陆失败
     * 4、如果存在→→→→→→使用jwt，生成token，返回给前端
     * 5、token放入redies中，redis里设置token：user信息，设置过期时间
     * （登陆认证的时候，先认证token字符串是否合法，再去redis认证是否存在）
     * @author YYTE_JX
     * @date 2021/10/3 0003
     */
    @Override
    public Result login(LoginParam loginParam) {
        /*1、检查参数是否合法*/
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            return Result.build(ResultCodeEnum.PARAM_ERROR.getCode(), ResultCodeEnum.PARAM_ERROR.getMessage());
        }

        /*2.1将密码加密*/
        String pwd = DigestUtils.md5Hex(password + slat);
        /*2.2、根据用户名和密码去user表中查询 是否存在*/
//        String pwd =password;
        SysUser sysUser = sysUserService.findUser(account,pwd);

        /*3、如果不存在→→→→→登陆失败*/
        if (sysUser == null) {
            return Result.build(ResultCodeEnum.LOGIN_FAIL.getCode(), ResultCodeEnum.LOGIN_FAIL.getMessage());
        }

        /*4、如果存在→→→→→→使用jwt，生成token，返回给前端*/
        String token = JWTUtils.createToken(sysUser.getId());
        /*5、token放入redies中，redis里设置token：user信息，设置过期时间*/
        String tokenIsExit = redisTemplate.opsForValue().get("TOKEN_" + token);
        System.out.println(JSON.toJSONString(sysUser));
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),12, TimeUnit.HOURS);
        String s = redisTemplate.opsForValue().get("TOKEN_" + token);
        System.out.println("==================="+s);
        return Result.success(token);
    }

    /**
     *  检查token
     * @author YYTE_JX
     * @date 2021/10/4 0004
     * @param token
     * @return com.jx.blogap1.dao.pojo.SysUser
     */
    @Override
    public SysUser checkToken(String token) {
        if (StringUtils.isBlank(token)){
            return null;
        }
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if (stringObjectMap == null){
            return null;
        }
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if (StringUtils.isBlank(userJson)){
            return null;
        }
        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
        return sysUser;
    }

    /**
     * 退出
     * @author YYTE_JX
     * @date 2021/10/4 0004
     * @param token
     * @return com.jx.blogap1.result.Result
     */
    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_"+token);
        return Result.success(null);

    }

    /**
     * 注册
     * @author YYTE_JX
     * @date 2021/10/4 0004
     * @param loginParam
     * @return com.jx.blogap1.result.Result
     */
    @Override
    public Result register(LoginParam loginParam) {
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        String nickname = loginParam.getNickname();
        String inviteCode = loginParam.getInviteCode();

        if (!(registerInviteCode.equals(inviteCode))){
//            System.out.println(inviteCode);
            //判断邀请码
            return Result.build(ResultCodeEnum.INVITE_CODE.getCode(), ResultCodeEnum.INVITE_CODE.getMessage());
        }
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password) || StringUtils.isBlank(nickname)) {
            return Result.build(ResultCodeEnum.PARAM_ERROR.getCode(), ResultCodeEnum.PARAM_ERROR.getMessage());
        }

        //通过account查找用户
        SysUser sysUser = this.sysUserService.findUserByAccount(account);
        if (sysUser != null) {
            return Result.build(ResultCodeEnum.ACCOUNT_EXIST.getCode(), ResultCodeEnum.ACCOUNT_EXIST.getMessage());
        }

        //用户不存在时注册
        sysUser = new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(DigestUtils.md5Hex(password+slat));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        sysUser.setAdmin(1); //1 为true
        sysUser.setDeleted(0); // 0 为false
        sysUser.setSalt("");
        sysUser.setStatus("");
        sysUser.setEmail("");
        this.sysUserService.save(sysUser);

        //存入token
        String token = JWTUtils.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),12, TimeUnit.HOURS);

        return Result.success(token);
    }
}
