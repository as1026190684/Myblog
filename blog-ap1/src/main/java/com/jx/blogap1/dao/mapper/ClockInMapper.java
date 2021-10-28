package com.jx.blogap1.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jx.blogap1.dao.pojo.ClockIn;
import org.springframework.stereotype.Repository;

@Repository
public interface ClockInMapper extends BaseMapper<ClockIn> {

    //打卡内容保存
    void insertClockIn(ClockIn clockIn);

    //获取某一天规划内容
    String getOneDayPlan(Long oneDayZeroTime);

//    void insertClockIn1(ClockIn clockIn);

}
