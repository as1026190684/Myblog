package com.jx.blogap1.controller;

import com.jx.blogap1.result.Result;
import com.jx.blogap1.service.SysUserService;
import com.jx.blogap1.vo.params.FilePath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
//@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*",methods = {})
public class UserinfoController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/currentUser")
    public Result currentUser(@RequestHeader("Authorization") String token) {
        return sysUserService.getUserInfoByToken(token);
    }

    @PostMapping("/updateAvatar")
    public Result updateAvatar(@RequestBody FilePath filePath, @RequestHeader("Authorization") String token) {
        System.out.println(filePath);
        return sysUserService.updateUserAvatarByToken(filePath,token);
    }
}
