package com.jx.blogap1.service;


import com.jx.blogap1.result.Result;
import com.jx.blogap1.vo.ArticleVo;
import com.jx.blogap1.vo.params.PageParams;

import java.util.List;

public interface ArticleService {

    List<ArticleVo> listArticlesPage(PageParams pageParams);

    //最热文章
    Result hotArticle(int limit);

    //最新文章
    Result newArticle(int limit);

    //文章归档
    Result listArchives();

    //文章详情
    ArticleVo findArticleById(Long id);
}