package com.jx.blogap1.controller;

import com.jx.blogap1.service.TagService;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.vo.TagVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("tags")
//@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*",methods = {})
public class TagController {
    @Autowired
    private TagService tagService;

    @Cacheable(value = "tags",keyGenerator = "keyGenerator")
    @GetMapping("/hot")
    public Result listHotTags() {
        int limit = 6;
        List<TagVo> tagVoList=tagService.hot(limit);
        return Result.success(tagVoList);
    }

    @Cacheable(value = "tags",keyGenerator = "keyGenerator")
    @GetMapping
    public Result findAll(){
        return tagService.findAll();
    }

    /**
     * 查询所有的标签
     * @author YYTE_JX
     * @date 2021/10/18 0018
     * @param
     * @return com.jx.blogap1.result.Result
     */
    @Cacheable(value = "tags",keyGenerator = "keyGenerator")
    @GetMapping("detail")
    public Result findAllDetail(){
        return tagService.findAllDetail();
    }

    /**
     * 标签文章列表
     * @author YYTE_JX
     * @date 2021/10/18 0018
     * @param id
     * @return com.jx.blogap1.result.Result
     */
    @Cacheable(value = "tags",keyGenerator = "keyGenerator")
    @GetMapping("detail/{id}")
    public Result findDetailById(@PathVariable("id") Long id){
        return tagService.findDetailById(id);
    }
}
