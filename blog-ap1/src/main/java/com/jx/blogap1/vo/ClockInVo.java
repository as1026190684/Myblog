package com.jx.blogap1.vo;

import lombok.Data;

@Data
public class ClockInVo {
    private Long id;

    private String content;

    private String createDate;

    private Long userId;

    private UserVo author;
}
