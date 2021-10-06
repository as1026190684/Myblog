package com.jx.blogap1.service;

import com.jx.blogap1.dao.mapper.ArticleMapper;
import com.jx.blogap1.dao.pojo.Article;

public interface ThreadService {

    void updateViewCount(ArticleMapper articleMapper, Article article);
}
