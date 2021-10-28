package com.jx.blogap1.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jx.blogap1.dao.mapper.ClockInMapper;
import com.jx.blogap1.dao.pojo.Article;
import com.jx.blogap1.dao.pojo.ClockIn;
import com.jx.blogap1.dao.pojo.SysUser;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.result.ResultCodeEnum;
import com.jx.blogap1.service.ClockInService;
import com.jx.blogap1.service.SysUserService;
import com.jx.blogap1.utils.TimeHandle;
import com.jx.blogap1.vo.*;
import com.jx.blogap1.vo.params.ClockInParams;
import com.jx.blogap1.vo.params.OneDayPlanParams;
import com.jx.blogap1.vo.params.PageParams;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
public class ClockInServiceImpl implements ClockInService {

    @Autowired
    private ClockInMapper clockInMapper;
    @Autowired
    private SysUserService sysUserService;
    /**
     * 打卡内容保存
     * @author YYTE_JX
     * @date 2021/10/8 0008
     * @param clockInParams
     * @return com.jx.blogap1.result.Result
     */

    @Override
    public Result saveClockInContent( ClockInParams clockInParams) {
        //----------------------------通过token来查询redis中的用户信息，再通过id进行查找打卡的时间
        SysUser sysUser = sysUserService.getUserInfoByToken1(clockInParams.getToken());
        if (ObjectUtils.isEmpty(sysUser)) {
            return Result.build(ResultCodeEnum.TOKEN_EXPIRED.getCode(), ResultCodeEnum.TOKEN_EXPIRED.getMessage());
        }
        Long userId = sysUser.getId();

        // ---------------------------- 通过逆序查询，id查询，CreateDate >todayZero?查询，来筛查当天是否打过卡
        Long todayZero = TimeHandle.getTodayZero(System.currentTimeMillis());
        LambdaQueryWrapper<ClockIn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(ClockIn::getUserId, ClockIn::getCreateDate);
        queryWrapper.eq(ClockIn::getUserId, userId);
        queryWrapper.ge(ClockIn::getCreateDate, todayZero);
        queryWrapper.last("limit 1");
        ClockIn clockInToday = clockInMapper.selectOne(queryWrapper);
        if (ObjectUtils.isNotEmpty(clockInToday)) {
            return Result.build(ResultCodeEnum.ALREADY_CLOCK_IN.getCode(), ResultCodeEnum.ALREADY_CLOCK_IN.getMessage());
        }

        //----------------------------有内容时存入，返回成功
        List<PlanVo> content = clockInParams.getContent();
        String string = JSONArray.toJSONString(content);
        ClockIn clockIn = new ClockIn();
        clockIn.setContent(string);
        clockIn.setCreateDate(System.currentTimeMillis()+28800000);
        System.out.println(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis()+28800000);

        clockIn.setUserId(userId);
        clockInMapper.insertClockIn(clockIn);
        return Result.success();
    }

    /**
     * 查找所有的打卡内容及用户
     * @author YYTE_JX
     * @date 2021/10/8 0008
     * @param
     * @return com.jx.blogap1.result.Result
     */

    @Override
    public List<ClockInVo> getClockInAll( ) {

        LambdaQueryWrapper<ClockIn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(ClockIn::getCreateDate);
        List<ClockIn> clockIns = clockInMapper.selectList(queryWrapper);
        List<ClockInVo> clockInVos = new ArrayList<>();

        for (ClockIn clockIn : clockIns) {
            ClockInVo clockInVo = new ClockInVo();
            BeanUtils.copyProperties(clockIn,clockInVo);

            clockInVo.setContent(JSON.parseArray(clockIn.getContent(), PlanVo.class));
            clockInVo.setCreateDate(new DateTime(clockIn.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
            //查找用户的基本信息
            if (clockInVo.getUserId() != null) {
                Long userId = clockInVo.getUserId();
                clockInVo.setAuthor(sysUserService.findUserVoById(userId));
            }
            //添加到List<ClockInVo>中
            clockInVos.add(clockInVo);
        }
        return clockInVos;

    }

    /**
     * 查找个人的打卡时间
     *
     * @param
     * @return com.jx.blogap1.result.Result
     * @author YYTE_JX
     * @date 2021/10/8 0008
     */

    @Override
    public Result getIndividualClockInByToken(String token) {

        //通过token来查询redis中的用户信息，再通过id进行查找打卡的时间
        SysUser sysUser = sysUserService.getUserInfoByToken1(token); //这里的非空判断在sysUserServiceImpl中已经做过
        if (sysUser== null) {
            return Result.build(ResultCodeEnum.TOKEN_EXPIRED.getCode(), ResultCodeEnum.TOKEN_EXPIRED.getMessage());
        }
        Long id = sysUser.getId();

        LambdaQueryWrapper<ClockIn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClockIn::getUserId, id);
        List<ClockIn> clockIns = clockInMapper.selectList(queryWrapper);

        //首次打卡前提示未曾打卡，同时检测空值，方便日后bug查快速询到此处
        List<ClockInVo> clockInVos = copyList(clockIns,sysUser,false);
        if (CollectionUtils.isEmpty(clockInVos)) {
            return Result.build(ResultCodeEnum.NO_CLOCK_IN.getCode(), ResultCodeEnum.NO_CLOCK_IN.getMessage());
        }
        List<String> createDateList = new ArrayList<>();
        for (ClockInVo clockInVo : clockInVos) {
            createDateList.add(clockInVo.getCreateDate());
        }
        return Result.success(createDateList);
    }

    /**
     * 获取某一天规划内容
     * @author YYTE_JX
     * @date 2021/10/17 0017
     * @param oneDayPlanParams
     * @return com.jx.blogap1.result.Result
     */
    @Override
    public Result getOneDayPlan(OneDayPlanParams oneDayPlanParams) {

        SysUser sysUser = sysUserService.getUserInfoByToken1(oneDayPlanParams.getToken());
        if (ObjectUtils.isEmpty(sysUser)) {
            return Result.build(ResultCodeEnum.TOKEN_EXPIRED.getCode(), ResultCodeEnum.TOKEN_EXPIRED.getMessage());
        }

        Long id = sysUser.getId();
        Long oneDayZeroTime = TimeHandle.toTimeStamp(oneDayPlanParams.getOneDayTime());
        String plan= clockInMapper.getOneDayPlan(oneDayZeroTime);

        List<PlanVo> planVos = JSON.parseArray(plan, PlanVo.class);
        ClockInVo clockInVo = new ClockInVo();
        clockInVo.setContent(planVos);

        return Result.success(clockInVo);
    }













    //----------------------------------------------------------------------------------------------------
//    private

    private ClockInVo copy(ClockIn clockIn,SysUser sysUser,boolean isAuthor) {
        ClockInVo clockInVo = new ClockInVo();
        BeanUtils.copyProperties(clockIn, clockInVo);
        // 时间格式化
        clockInVo.setCreateDate(new DateTime(clockIn.getCreateDate()).toString("yyyy-MM-dd"));
        //传入userVo对象
        if (isAuthor) {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(sysUser,userVo);
            clockInVo.setAuthor(userVo);
        }
        return clockInVo;
    }
    private List<ClockInVo> copyList(List<ClockIn> clockIns,SysUser sysUser,boolean isAuthor) {
        List<ClockInVo> clockInVoList = new ArrayList<>();
        for (ClockIn clockIn : clockIns) {
            clockInVoList.add(copy(clockIn,sysUser,isAuthor));
        }
        return clockInVoList;
    }


    //----------------------------------------------------------------------------------------------------------
    @Override
    public Result pushIndividualDayPlan(ClockInParams clockInParams) {
//        List<PlanVo> content = clockInParams.getContent();
//        String string = JSONArray.toJSONString(content);
////        System.out.println(string);
//        ClockIn clockIn = new ClockIn();
//        clockIn.setContent(string);
//        clockInMapper.insertClockIn1(clockIn);
//
//        LambdaQueryWrapper<ClockIn> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(ClockIn::getUserId, 1);
//        ClockIn clockIn1 = clockInMapper.selectOne(queryWrapper);
//        List<PlanVo> planVos = JSON.parseArray(clockIn1.getContent(), PlanVo.class);
//        ClockInVo clockInVo = new ClockInVo();
//        clockInVo.setContent(planVos);
//        return Result.success(clockInVo);
        return Result.success();
    }


}
