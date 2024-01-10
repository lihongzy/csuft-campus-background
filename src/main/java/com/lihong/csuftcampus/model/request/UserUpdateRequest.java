package com.lihong.csuftcampus.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新类
 */
@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = -254514408772907139L;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别 0-男 1-女
     */
    private Integer gender;

    /**
     * 入学年级
     */
    private String grade;

    /**
     * 学院
     */
    private String college;

    /**
     * 专业
     */
    private String profession;

    /**
     * 爱好
     */
    private String hobby;
}
