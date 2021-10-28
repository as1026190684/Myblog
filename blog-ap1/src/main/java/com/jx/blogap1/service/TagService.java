package com.jx.blogap1.service;

import com.jx.blogap1.result.Result;
import com.jx.blogap1.vo.TagVo;

import java.util.List;

public interface TagService {
    List<TagVo> findTagsByArticleId(Long articleId);

    /**
     * 最热门标签
     * @author YYTE_JX
     * @date 2021/10/2 0002
     * @param limit
     * @return java.util.List<com.jx.blogap1.vo.TagVo>
     */
    List<TagVo> hot(int limit);

    //写文章 选择标签
    Result findAll();

    //查询所有的标签
    Result findAllDetail();

    //标签文章列表
    Result findDetailById(Long id);
}
