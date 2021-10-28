package com.jx.blogap1.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jx.blogap1.dao.mapper.ArticleMapper;
import com.jx.blogap1.dao.pojo.Article;
import com.jx.blogap1.utils.RedisDBChangeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Component
public class ScheduledMethods {

    @Autowired
    private RedisDBChangeUtil redisDBChangeUtil;
    @Autowired
    private ArticleMapper articleMapper;

    @Async("taskExecutor")
    @Scheduled(cron = "0 0 4 * * ?")
    public void deleteClockInRedisPlanCache() {
        String key = "clockIn:plan";
        boolean b = redisDBChangeUtil.delFuzzy(key);
    }

    @Async("taskExecutor")
    @Scheduled(cron = "0 0 3 * * ?")
    public void updateViewCountRegularly() {
//        Article article = new Article();
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","view_counts");
        List<Article> list = articleMapper.selectList(queryWrapper);
        for (Article article : list) {
            boolean hasKey = redisDBChangeUtil.hasKey("ViewCounts:"+article.getId().toString());
            if (hasKey) {
                LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper();
                Integer viewCounts = (Integer) redisDBChangeUtil.get("ViewCounts:"+article.getId().toString());
                article.setViewCounts(viewCounts);
                lambdaQueryWrapper.eq(Article::getId, article.getId());
                articleMapper.update(article,lambdaQueryWrapper);
            }
        }
    }
}
