package com.jx.blogap1.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;


import java.util.List;

@Data
public class ClockInVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private List<PlanVo> content;

    private String createDate;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private UserVo author;
}
