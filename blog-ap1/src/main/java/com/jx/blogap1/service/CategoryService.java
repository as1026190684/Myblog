package com.jx.blogap1.service;


import com.jx.blogap1.result.Result;
import com.jx.blogap1.vo.CategoryVo;

public interface CategoryService {

    CategoryVo findCategoryById(Long id);

    Result findAll();


    // 查询所有的文章分类
    Result findAllDetail();

    //分类文章列表
    Result categoriesDetailById(Long id);
}