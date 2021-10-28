package com.jx.blogap1.vo.params;

import com.jx.blogap1.vo.PlanVo;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@Data
public class ClockInParams {
//    private Integer id;

//    @NotNull
    private List<PlanVo> content;

//    private Long createDate;

//    @NotBlank(message = "不能为空")
    private String token;

}
