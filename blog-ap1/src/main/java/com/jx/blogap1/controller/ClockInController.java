package com.jx.blogap1.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.result.ResultCodeEnum;
import com.jx.blogap1.service.ClockInService;
import com.jx.blogap1.utils.RedisDBChangeUtil;
import com.jx.blogap1.utils.UserThreadLocal;
import com.jx.blogap1.vo.ArticleVo;
import com.jx.blogap1.vo.ClockInVo;
import com.jx.blogap1.vo.PlanVo;
import com.jx.blogap1.vo.params.ClockInParams;
import com.jx.blogap1.vo.params.OneDayPlanParams;
import com.jx.blogap1.vo.params.PageParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("clockIn")
public class ClockInController {

    @Autowired
    private ClockInService clockInService;
    @Autowired
    private RedisDBChangeUtil redisDBChangeUtil;
    /**
     * 打卡内容保存
     *
     * @param clockInParams
     * @return com.jx.blogap1.result.Result
     * @author YYTE_JX
     * @date 2021/10/8 0008
     */
    @CacheEvict(value = "clockIn", allEntries=true)
    @PostMapping("/save")
    public Result clockInSave(@RequestBody ClockInParams clockInParams) {
        //----------------------------判断打卡内容为空时直接返回'参数有误'
        if (StringUtils.isBlank(clockInParams.getToken())) {
            return Result.build(ResultCodeEnum.TOKEN_EXPIRED.getCode(), ResultCodeEnum.TOKEN_EXPIRED.getMessage());
        }
        if (CollectionUtils.isEmpty(clockInParams.getContent())){
            return Result.build(ResultCodeEnum.CONTENT_IS_EMPTY.getCode(), ResultCodeEnum.CONTENT_IS_EMPTY.getMessage());
        }

        return clockInService.saveClockInContent(clockInParams);
    }
    /**
     * 打卡内容缓存--------redis
     *
     * @param clockInParams
     * @return com.jx.blogap1.result.Result
     * @author YYTE_JX
     * @date 2021/10/8 0008
     */
//    @CacheEvict(value = "clockIn", allEntries=true)
    @PostMapping("/putPlanToRedis")
    public Result putPlanToRedis(@RequestBody ClockInParams clockInParams) {
        //----------------------------判断打卡内容为空时直接返回'参数有误'
        if (StringUtils.isBlank(clockInParams.getToken())) {
            return Result.build(ResultCodeEnum.TOKEN_EXPIRED.getCode(), ResultCodeEnum.TOKEN_EXPIRED.getMessage());
        }
        if (CollectionUtils.isEmpty(clockInParams.getContent())){
            return Result.build(ResultCodeEnum.CONTENT_IS_EMPTY.getCode(), ResultCodeEnum.CONTENT_IS_EMPTY.getMessage());
        }
        String key = "clockIn:plan:ClockInController.PutPlanToRedis" + clockInParams.getToken();
        boolean b = redisDBChangeUtil.hasKey(key);
        if (b) {
            redisDBChangeUtil.del(key);
            boolean set = redisDBChangeUtil.set(key, JSONArray.toJSONString(clockInParams.getContent()), 12 * 60 * 60);
            if (set) {
                return Result.success();
            }
            return Result.build(ResultCodeEnum.CLOCK_IN_ERROR.getCode(), ResultCodeEnum.CLOCK_IN_ERROR.getMessage());
        }
        boolean set1 = redisDBChangeUtil.set(key, JSONArray.toJSONString(clockInParams.getContent()), 12 * 60 * 60);
        if (set1) {
            return Result.success();
        }
        return Result.build(ResultCodeEnum.CLOCK_IN_ERROR.getCode(), ResultCodeEnum.CLOCK_IN_ERROR.getMessage());
    }
    /**
     * 查询redis中打卡的内容缓存
     * @author YYTE_JX
     * @date 2021/10/22 0022
     * @param token
     * @return com.jx.blogap1.result.Result
     */
    @GetMapping("/queryPlanCache")
    public Result queryPlanCache(@RequestHeader("Authorization") String token) {
        String key = "clockIn:plan:ClockInController.PutPlanToRedis" + token;
        boolean b = redisDBChangeUtil.hasKey(key);
        if (b) {
            String listString= redisDBChangeUtil.getString(key);
            List<PlanVo> planVoList = JSON.parseArray(listString, PlanVo.class);
            if (!CollectionUtils.isEmpty(planVoList)) {
                return Result.success(planVoList);
            }
            return Result.build(ResultCodeEnum.CLOCK_IN_ERROR.getCode(), ResultCodeEnum.CLOCK_IN_ERROR.getMessage());
        }
        return Result.build(ResultCodeEnum.NO_CLOCK_IN.getCode(), ResultCodeEnum.NO_CLOCK_IN.getMessage());
    }


    /**
     * 查找所有的打卡内容及用户
     *
     * @param
     * @return com.jx.blogap1.result.Result
     * @author YYTE_JX
     * @date 2021/10/8 0008
     */
    @Cacheable(value = "clockIn",keyGenerator = "keyGenerator")
    @GetMapping("/getAllClockIn")
    public Result getClockInBy() {

        List<ClockInVo> clockInVoList = clockInService.getClockInAll();
        return Result.success(clockInVoList);
    }

    /**
     * 查找个人的打卡时间
     *
     * @param
     * @return com.jx.blogap1.result.Result
     * @author YYTE_JX
     * @date 2021/10/8 0008
     */
    //todo 加上 @Cacheable，会把controller里执行返回的信息也存入redis中，例如此中的ResultCodeEnum
    @Cacheable(value = "clockIn",keyGenerator = "keyGenerator")
    @GetMapping("/getIndividualClockIn")
    public Result getIndividualClockIn(@RequestHeader("Authorization") String token) {
        if (StringUtils.equals(token, "undefined")) {
            return  Result.build(ResultCodeEnum.TOKEN_EXPIRED.getCode(), ResultCodeEnum.TOKEN_EXPIRED.getMessage());
        }
        return clockInService.getIndividualClockInByToken(token);
    }

    /**
     * 获取某一天规划内容
     * @author YYTE_JX
     * @date 2021/10/17 0017
     * @param oneDayPlanParams
     * @return com.jx.blogap1.result.Result
     */
    @Cacheable(value = "clockIn",keyGenerator = "keyGenerator")
    @PostMapping("/getOneDayPlan")
    public Result getOneDayPlan(@RequestBody OneDayPlanParams oneDayPlanParams) {
        //----------------------------判断打卡内容为空时直接返回'参数有误'
        if (StringUtils.isBlank(oneDayPlanParams.getToken())) {
            return  Result.build(ResultCodeEnum.TOKEN_EXPIRED.getCode(), ResultCodeEnum.TOKEN_EXPIRED.getMessage());
        }
        if (StringUtils.isBlank(oneDayPlanParams.getOneDayTime())){
            return Result.build(ResultCodeEnum.CONTENT_IS_EMPTY.getCode(), ResultCodeEnum.CONTENT_IS_EMPTY.getMessage());
        }
        return clockInService.getOneDayPlan(oneDayPlanParams);
    }




//    @PostMapping("pushIndividualDayPlan")
//    public Result pushIndividualDayPlan(@RequestBody ClockInParams clockInParams) {
//
//        return clockInService.pushIndividualDayPlan(clockInParams);
//
//    }
}
