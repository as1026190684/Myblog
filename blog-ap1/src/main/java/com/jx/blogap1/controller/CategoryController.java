package com.jx.blogap1.controller;

import com.jx.blogap1.result.Result;

import com.jx.blogap1.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("categorys")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Cacheable(value = "categorys",keyGenerator = "keyGenerator")
    @GetMapping
    public Result listCategory() {
        return categoryService.findAll();
    }

    /**
     * 查询所有的文章分类
     * @author YYTE_JX
     * @date 2021/10/18 0018
     * @param
     * @return com.jx.blogap1.result.Result
     */
    @Cacheable(value = "categorys",keyGenerator = "keyGenerator")
    @GetMapping("detail")
    public Result categoriesDetail(){
        return categoryService.findAllDetail();
    }

    /**
     * 分类文章列表
     * @author YYTE_JX
     * @date 2021/10/18 0018
     * @param id
     * @return com.jx.blogap1.result.Result
     */
    @Cacheable(value = "categorys",keyGenerator = "keyGenerator")
    @GetMapping("detail/{id}")
    public Result categoriesDetailById(@PathVariable("id") Long id){
        return categoryService.categoriesDetailById(id);
    }
}
