package com.jx.blogap1.service.impl;

import com.jx.blogap1.dao.mapper.ArticleMapper;
import com.jx.blogap1.dao.pojo.Article;
import com.jx.blogap1.service.ThreadService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ThreadServiceImpl implements ThreadService {


    /**
     * 使用线程池异步操作阅读次数（异步以免操作错误造成无法查看文章）
     * @author YYTE_JX
     * @date 2021/10/4 0004
     * @param articleMapper
     * @param article
     * @return void
     */
    @Async("taskExecutor")
    @Override
    public void updateViewCount(ArticleMapper articleMapper, Article article){

        Article articleUpdate = new Article();
        articleUpdate.setViewCounts(article.getViewCounts() + 1);
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,article.getId());
        queryWrapper.eq(Article::getViewCounts,article.getViewCounts());
        articleMapper.update(articleUpdate,queryWrapper);
        try {
            //睡眠5秒 证明不会影响主线程的使用
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}