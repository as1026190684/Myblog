package com.jx.blogap1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jx.blogap1.dao.mapper.ArticleMapper;
import com.jx.blogap1.dao.pojo.Article;
import com.jx.blogap1.service.ThreadService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jx.blogap1.utils.RedisDBChangeUtil;
import com.jx.blogap1.vo.ArticleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ThreadServiceImpl implements ThreadService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private RedisDBChangeUtil redisDBChangeUtil;
    /**
     * 使用线程池异步操作阅读次数（异步以免操作错误造成无法查看文章）
     * @author YYTE_JX
     * @date 2021/10/4 0004
     * @param articleMapper
     * @param articleVo
     * @return void
     */
//    @Cacheable(value = "readCounts",keyGenerator = "keyGenerator")
    @Async("taskExecutor")
    @Override
    public Integer updateViewCount(ArticleMapper articleMapper, ArticleVo articleVo){
        boolean hasKey = redisDBChangeUtil.hasKey("ViewCounts:"+articleVo.getId().toString());
        if (!hasKey) {
            redisDBChangeUtil.set("ViewCounts:" + articleVo.getId().toString(), 1);
        }
        redisDBChangeUtil.incr1("ViewCounts:"+articleVo.getId().toString(), 1);
        Integer viewCounts = (Integer) redisDBChangeUtil.get("ViewCounts:" + articleVo.getId().toString());
        try {
            //睡眠1秒 证明不会影响主线程的使用
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return viewCounts;
    }



}