package com.jx.blogap1.dao.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jx.blogap1.vo.PlanVo;
import lombok.Data;

import java.util.List;

@Data
public class ClockIn {


    private Long id;

    private String content;

    private Long createDate;

    private Long userId;


}
