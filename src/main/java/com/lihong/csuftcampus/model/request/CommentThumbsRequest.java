package com.lihong.csuftcampus.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论点赞请求
 */
@Data
public class CommentThumbsRequest implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = 935627460548801727L;

    /**
     * 评论Id
     */
    private Long commentId;
}
