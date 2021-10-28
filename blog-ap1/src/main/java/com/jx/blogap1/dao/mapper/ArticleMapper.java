package com.jx.blogap1.dao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jx.blogap1.dao.dos.Archives;
import com.jx.blogap1.dao.pojo.Article;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleMapper extends BaseMapper<Article> {
    //最热文章
    List<Article> findHotArticleByViewCounts(int limit);

    //最新文章
    List<Article> findNewArticleByCreateTime(int limit);

    //文章归档
    List<Archives> listArchives();

    //文章列表
    IPage<Article> listArticle(Page<Article> page, Long categoryId, Long tagId, String year, String month);
}