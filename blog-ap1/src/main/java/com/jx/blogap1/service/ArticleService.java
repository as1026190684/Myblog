package com.jx.blogap1.service;


import com.jx.blogap1.result.Result;
import com.jx.blogap1.vo.ArticleVo;
import com.jx.blogap1.vo.params.ArticleParam;
import com.jx.blogap1.vo.params.PageParams;

import java.util.List;

public interface ArticleService {

    Result listArticlesPage(PageParams pageParams);

    //最热文章
    Result hotArticle(int limit);

    //最新文章
    Result newArticle(int limit);

    //文章归档
    Result listArchives();

    //文章详情
    ArticleVo findArticleById(Long id);

    //发布文章
    Result publish(ArticleParam articleParam);

    //更新前获取文章
    ArticleVo findArticleByIdToUpdate(Long id);

    //更新文章
    Result update(ArticleParam articleParam);

    //获取个人的所有文字
    Result getArticlesByUserId(Long id);

}