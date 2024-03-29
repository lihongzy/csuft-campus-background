package com.lihong.csuftcampus.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户请求类
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -5857724311362240407L;

    /**
     * 学号（全局唯一）
     */
    private String userAccount;

    /**
     * 密码（加密存储）
     */
    private String userPassword;

}
