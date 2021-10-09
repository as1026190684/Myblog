package com.jx.blogap1.service;


import com.jx.blogap1.result.Result;
import com.jx.blogap1.vo.params.ClockInParams;

public interface ClockInService {

    //打卡内容保存
    Result saveClockInContent(ClockInParams clockInParams);

    //查找所有的打卡内容及用户
    Result getClockInAll();

    //查找个人的打卡时间
    Result getIndividualClockInByToken(String token);
}
