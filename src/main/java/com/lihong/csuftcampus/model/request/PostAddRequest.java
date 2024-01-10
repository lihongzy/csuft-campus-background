package com.lihong.csuftcampus.model.request;

import com.lihong.csuftcampus.model.domain.Tag;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 帖子添加请求
 */

@Data
public class PostAddRequest  implements Serializable {
    private static final long serialVersionUID = -5166955030779353111L;

    /**
     * 发帖内容
     */
    private String content;

    /**
     * 审核状态 0-待审核 1-通过 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 关联标签列表
     */
    private List<Tag> tags;

}
