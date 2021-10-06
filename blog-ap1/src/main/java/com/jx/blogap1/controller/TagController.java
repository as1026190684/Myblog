package com.jx.blogap1.controller;

import com.jx.blogap1.service.TagService;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.vo.TagVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("tags")
//@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*",methods = {})
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping("/hot")
    public Result listHotTags() {
        int limit = 6;
        List<TagVo> tagVoList=tagService.hot(limit);
        return Result.success(tagVoList);

    }
}
