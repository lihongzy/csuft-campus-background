package com.lihong.csuftcampus.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子评论表
 */
@TableName(value = "post_comments")
@Data
public class PostComments implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 评论用户ID
     */

    private Long userId;

    /**
     * 帖子ID
     */

    private Long postId;

    /**
     * 评论内容
     */

    private String content;

    /**
     * 点赞数
     */

    private Integer thumbNum;

    /**
     * 状态 0：正常，1：被举报，2：禁止查看
     */
    private Integer status;

    /**
     * 创建时间
     */

    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private Boolean hasThumb;

}