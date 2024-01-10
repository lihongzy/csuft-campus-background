package com.lihong.csuftcampus.model.vo;

import com.lihong.csuftcampus.model.domain.PostComments;
import com.lihong.csuftcampus.model.dto.UserDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 帖子评论VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostCommentsVO extends PostComments implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = -2072476628118289151L;

    /**
     * 用户评论
     */
    private UserDTO commentUser;

}
