package com.jx.blogap1.controller;


import com.jx.blogap1.result.Result;
import com.jx.blogap1.result.ResultCodeEnum;
import com.jx.blogap1.utils.QiniuUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("upload")
public class UploadController {
    @Autowired
    private QiniuUtils qiniuUtils;

    /**
     * 上传图片
     * @author YYTE_JX
     * @date 2021/10/21 0021
     * @param file
     * @return com.jx.blogap1.result.Result(图片地址  url)
     */
    @PostMapping
    public Result upload(@RequestParam("file") MultipartFile file){
//        String fileName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
        if (file.isEmpty()){
            return Result.build(ResultCodeEnum.UP_LOAD_ERROR.getCode(),ResultCodeEnum.UP_LOAD_ERROR.getMessage());
        }
        try {
            String url = qiniuUtils.saveImage(file);
            if ("TypeError".equals(url)) {
                return Result.build(ResultCodeEnum.Qi_Niu_Error.getCode(), ResultCodeEnum.Qi_Niu_Error.getMessage());
            }
            if (StringUtils.isBlank(url)) {
                return Result.build(ResultCodeEnum.UP_LOAD_ERROR.getCode(),ResultCodeEnum.UP_LOAD_ERROR.getMessage());
            }
            return Result.success(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.build(ResultCodeEnum.UP_LOAD_ERROR.getCode(),ResultCodeEnum.UP_LOAD_ERROR.getMessage());
    }

    //todo 第一次修改头像报错 QiniuException：no such file
    @GetMapping("/delete/{name}")
    public Result delete(@PathVariable("name") String fileName) {
        int i = qiniuUtils.deleteFileFromQiNiu(fileName);
        return Result.success(i);
    }
}
