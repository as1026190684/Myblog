package com.jx.blogap1.vo.params;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginParam implements Serializable {

    private String account;

    private String password;

    private String nickname;
}
