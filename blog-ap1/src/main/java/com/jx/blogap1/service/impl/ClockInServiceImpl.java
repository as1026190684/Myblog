package com.jx.blogap1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jx.blogap1.dao.mapper.ClockInMapper;
import com.jx.blogap1.dao.pojo.ClockIn;
import com.jx.blogap1.dao.pojo.SysUser;
import com.jx.blogap1.result.Result;
import com.jx.blogap1.result.ResultCodeEnum;
import com.jx.blogap1.service.ClockInService;
import com.jx.blogap1.service.SysUserService;
import com.jx.blogap1.vo.*;
import com.jx.blogap1.vo.params.ClockInParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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
    public Result saveClockInContent(ClockInParams clockInParams) {
        //判断打卡内容为空时直接返回'参数有误'
        if (clockInParams.getContent() == null) {
            return Result.build(ResultCodeEnum.PARAMS_ERROR.getCode(),ResultCodeEnum.PARAMS_ERROR.getMessage());
        }
        //有内容时存入，返回成功
        ClockIn clockIn = new ClockIn();
        BeanUtils.copyProperties(clockInParams, clockIn);
        clockIn.setCreateDate(System.currentTimeMillis());

        clockInMapper.insert(clockIn);

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
    public Result getClockInAll() {
        LambdaQueryWrapper<ClockIn> queryWrapper = new LambdaQueryWrapper<>();

        List<ClockIn> clockIns = clockInMapper.selectList(queryWrapper);
        List<ClockInVo> clockInVos = new ArrayList<>();

        ClockInVo clockInVo = new ClockInVo();
        for (ClockIn clockIn : clockIns) {
            BeanUtils.copyProperties(clockIn,clockInVo);
            //查找用户的基本信息
            if (clockInVo.getUserId() != null) {
                Long userId = clockInVo.getUserId();
                clockInVo.setAuthor(sysUserService.findUserVoById(userId));
            }
            //添加到List<ClockInVo>中
            clockInVos.add(clockInVo);
        }
        return Result.success(clockInVos);

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

        Long id = sysUser.getId();
        if (id == null) {
            return Result.build(ResultCodeEnum.FETCH_USERINFO_ERROR.getCode(), ResultCodeEnum.FETCH_USERINFO_ERROR.getMessage());
        }

        LambdaQueryWrapper<ClockIn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClockIn::getUserId, id);
        List<ClockIn> clockIns = clockInMapper.selectList(queryWrapper);

        //首次打卡前提示未曾打卡，同时检测空值，方便日后bug查快速询到此处
        List<ClockInVo> clockInVos = copyList(clockIns);
        if (CollectionUtils.isEmpty(clockInVos)) {
            return Result.build(ResultCodeEnum.NO_CLOCK_IN.getCode(), ResultCodeEnum.NO_CLOCK_IN.getMessage());
        }

        return Result.success(clockInVos);
    }

    //----------------------------------------------------------------------------------------------------
    private ClockInVo copy(ClockIn clockIn) {
        ClockInVo clockInVo = new ClockInVo();
        BeanUtils.copyProperties(clockIn, clockInVo);
        // 时间格式化
        clockInVo.setCreateDate(new DateTime(clockIn.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        return clockInVo;

    }
    private List<ClockInVo> copyList(List<ClockIn> clockIns) {
        List<ClockInVo> clockInVoList = new ArrayList<>();
        for (ClockIn clockIn : clockIns) {
            clockInVoList.add(copy(clockIn));
        }
        return clockInVoList;
    }

}
