package com.jx.blogap1.vo;

import lombok.Data;

@Data
public class LoginUserVo {

    private Long id;

    private String account;

    private String nickname;

    private String avatar;
}