package com.lihong.csuftcampus.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * 脱敏用户实体类
 */
@Data
public class UserDTO {
    //id
    private Long id;

    //用户学号
    private String userAccount;

    //用户姓名
    private String username;

    //用户昵称
    private String nickname;

    //用户头像
    private String avatar;

    //性别
    private Integer gender;

    //年级
    private String grade;

    //学院
    private String college;

    //专业
    private String profession;

    //兴趣
    private String hobby;

    //状态
    private Integer userStatus;

    //用户角色
    private Integer userRole;

    //创建时间
    private Date createTime;

    //是否关注
    private Boolean isFollow;

}
