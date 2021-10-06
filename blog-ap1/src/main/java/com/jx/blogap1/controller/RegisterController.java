package com.jx.blogap1.controller;


import com.jx.blogap1.result.Result;
import com.jx.blogap1.service.LoginService;
import com.jx.blogap1.vo.params.LoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("register")
//@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*",methods = {})
public class RegisterController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public Result register(@RequestBody LoginParam loginParam) {

        return loginService.register(loginParam);
    }
}
