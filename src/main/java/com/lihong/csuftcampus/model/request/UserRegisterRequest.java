package com.lihong.csuftcampus.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求封装类
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = -2604285553140424388L;

    /**
     * 学号（全局唯一）
     */
    private String userAccount;

    /**
     * 用户姓名
     */
    private String username;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;

}
