package com.jx.blogap1.service;

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
}
