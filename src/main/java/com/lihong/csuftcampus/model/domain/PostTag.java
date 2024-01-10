package com.lihong.csuftcampus.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子标签关联表
 */
@Data
@TableName(value = "post_tag")
public class PostTag implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)

    private Long id;

    /**
     * 帖子ID
     */

    private Long postId;

    /**
     * 标签ID
     */

    private Long tagId;

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
}