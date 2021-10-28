package com.jx.blogap1.service;


import com.jx.blogap1.result.Result;
import com.jx.blogap1.vo.ClockInVo;
import com.jx.blogap1.vo.params.ClockInParams;
import com.jx.blogap1.vo.params.OneDayPlanParams;
import com.jx.blogap1.vo.params.PageParams;

import java.util.List;

public interface ClockInService {

    //打卡内容保存
    Result saveClockInContent(ClockInParams clockInParams);

    //查找所有的打卡内容及用户
    List<ClockInVo> getClockInAll();

    //查找个人的打卡时间
    Result getIndividualClockInByToken(String token);


    Result pushIndividualDayPlan(ClockInParams clockInParams);

    //查找某一天的规划 plan
    Result getOneDayPlan(OneDayPlanParams oneDayPlanParams);
}
