package com.lihong.csuftcampus.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 *帖子添加评论请求
 */
@Data
public class AddCommentRequest implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = 1626243861114429638L;

    /**
     * 帖子id
     */
    private Long postId;
    /**
     * 内容
     */
    private String content;
}
