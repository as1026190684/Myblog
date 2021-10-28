package com.jx.blogap1.service;

import com.jx.blogap1.dao.mapper.ArticleMapper;
import com.jx.blogap1.dao.pojo.Article;
import com.jx.blogap1.vo.ArticleVo;

public interface ThreadService {

    Integer updateViewCount(ArticleMapper articleMapper, ArticleVo articleVo);

//    void updateViewCountRegularly();
}
