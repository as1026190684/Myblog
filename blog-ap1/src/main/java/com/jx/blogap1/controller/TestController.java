package com.jx.blogap1.controller;


import com.jx.blogap1.dao.pojo.SysUser;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.utils.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("test")
@Slf4j
//@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*",methods = {})
public class TestController {

//    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    @RequestMapping
    public Result test(){
        SysUser sysUser = UserThreadLocal.get();
        System.out.println(sysUser);
//        logger.error("=====error=====");
        log.error("=====error=====");
        return Result.success();

    }
}