package com.jx.blogap1.config;

import com.jx.blogap1.handler.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * 跨域
     * @author YYTE_JX
     * @date 2021/10/1 0001
     * @param registry
     * @return void
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins("*");
    }

    //todo 个人中心修改头像如何加上登陆限制
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/test")
                .addPathPatterns("/comments/create/change")
                .addPathPatterns("/clockIn")
                .addPathPatterns("/clockIn/putPlanToRedis")
                .addPathPatterns("/clockIn/queryPlanCache")
                .addPathPatterns("/articles/publish")
                .addPathPatterns("/articles/update")
                .addPathPatterns("/users/updateAvatar");
    }
}
