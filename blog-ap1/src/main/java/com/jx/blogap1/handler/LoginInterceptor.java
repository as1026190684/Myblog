package com.jx.blogap1.handler;

import com.alibaba.fastjson.JSON;

import com.jx.blogap1.dao.pojo.SysUser;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.result.ResultCodeEnum;
import com.jx.blogap1.service.LoginService;
import com.jx.blogap1.utils.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService loginService;

    /**在执行controller方法(Handler)之前进行执行
     * 1. 需要判断 请求的接口路径 是否为 HandlerMethod (controller方法)
     * 2. 判断 token是否为空，如果为空 未登录
     * 3. 如果token 不为空，登录验证 loginService checkToken
     * 4. 如果认证成功 放行即可
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String token = request.getHeader("Authorization");
        //lombok log
        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");


        if (token == null) {
            Result<Object> result = Result.build(ResultCodeEnum.NO_LOGIN.getCode(), ResultCodeEnum.NO_LOGIN.getMessage());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }

        SysUser sysUser =  loginService.checkToken(token);
        if (sysUser == null) {
            Result<Object>  result = Result.build(ResultCodeEnum.NO_LOGIN.getCode(), ResultCodeEnum.NO_LOGIN.getMessage());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }

        //登录验证成功，放行
        //我希望在controller中 直接获取用户的信息 怎么获取?
        UserThreadLocal.put(sysUser);
        //最终放行
        return true;
    }

    /**
     *在执行controller方法(Handler)之后进行执行
     * @author YYTE_JX
     * @date 2021/10/4 0004
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @return void
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserThreadLocal.remove();
    }
}