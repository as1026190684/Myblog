package com.jx.blogap1.service.impl;

import com.jx.blogap1.dao.mapper.CategoryMapper;
import com.jx.blogap1.dao.pojo.Category;
import com.jx.blogap1.service.CategoryService;
import com.jx.blogap1.vo.CategoryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public CategoryVo findCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category, categoryVo);
        return categoryVo;
    }
}
