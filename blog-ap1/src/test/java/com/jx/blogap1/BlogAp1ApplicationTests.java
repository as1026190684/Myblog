package com.jx.blogap1;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.jx.blogap1.dao.mapper.ArticleMapper;
import com.jx.blogap1.dao.pojo.Article;
import com.jx.blogap1.dao.pojo.ClockIn;
import com.jx.blogap1.utils.RedisDBChangeUtil;
import com.jx.blogap1.utils.TimeHandle;
import com.jx.blogap1.vo.PlanVo;
import com.jx.blogap1.vo.params.ClockInParams;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
class BlogAp1ApplicationTests {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private RedisDBChangeUtil redisDBChangeUtil;

    @Test
    void contextLoads() {

//        PlanVo planVo = new PlanVo();
//        planVo.setText("j1y2tj1y54w6456");
//        planVo.setComplete(true);
//        PlanVo planVo1 = new PlanVo();
//        planVo1.setText("15re6561456g15r6ea15g6rae1");
//        planVo1.setComplete(true);
////        mongoTemplate.insert(planVo);
//        List<PlanVo> list = new ArrayList<>();
//        list.add(planVo);
//        list.add(planVo1);
//        ClockIn clockIn = new ClockIn();
//        clockIn.setUserId(123L);
//        clockIn.setCreateDate(System.currentTimeMillis());
//        clockIn.setContent(list);
//        mongoTemplate.insert(clockIn);

//        ClockInParams clockInParams = new ClockInParams();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parse = sdf.parse("2021-10-17");
            long time = parse.getTime();
            long t = 1634373467089L;
            long tf = t + 86400000L;
            System.out.println(parse.getTime());
//            System.out.println(TimeHandle.getTodayZero(time));
//
//            if (time >= t && time <= tf) {
//                System.out.println("正确");
//            } else {
//                System.out.println("错误");
//            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Test
    void redisTest() {
        LettuceConnectionFactory connectionFactory = (LettuceConnectionFactory) stringRedisTemplate.getConnectionFactory();
        if (connectionFactory != null ) {
            //切换DB
            connectionFactory.setDatabase(2);
            //是否允许多个线程操作共用同一个缓存连接，默认 true，false 时每个操作都将开辟新的连接
            connectionFactory.setShareNativeConnection(false);
            this.stringRedisTemplate.setConnectionFactory(connectionFactory);
            connectionFactory.resetConnection();
            stringRedisTemplate.opsForValue().set("222", "11111");
        }

    }

    @Test
    public void viewCounts() {

    }
}
