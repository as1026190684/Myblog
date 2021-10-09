package com.jx.blogap1.controller;

import com.jx.blogap1.result.Result;
import com.jx.blogap1.service.ClockInService;
import com.jx.blogap1.utils.UserThreadLocal;
import com.jx.blogap1.vo.params.ClockInParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("clockIn")
public class ClockInController {

    @Autowired
    private ClockInService clockInService;

    /**
     * 打卡内容保存
     *
     * @param clockInParams
     * @return com.jx.blogap1.result.Result
     * @author YYTE_JX
     * @date 2021/10/8 0008
     */
    @PostMapping("/save")
    public Result clockInSave(@RequestBody ClockInParams clockInParams) {

        return clockInService.saveClockInContent(clockInParams);
    }

    /**
     * 查找所有的打卡内容及用户
     *
     * @param
     * @return com.jx.blogap1.result.Result
     * @author YYTE_JX
     * @date 2021/10/8 0008
     */
    @GetMapping("/getAllClockIn")
    public Result getClockInBy() {

        return clockInService.getClockInAll();
    }

    /**
     * 查找个人的打卡时间
     * @author YYTE_JX
     * @date 2021/10/8 0008
     * @param
     * @return com.jx.blogap1.result.Result
     */
    @GetMapping("/getIndividualClockIn")
    public Result getIndividualClockIn(@RequestHeader("Authorization") String token) {
        return clockInService.getIndividualClockInByToken(token);
    }
}
