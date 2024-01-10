package com.lihong.csuftcampus.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改标签颜色请求类
 */
@Data
public class ChangeColorRequest implements Serializable {

    private static final long serialVersionUID = 5233931486090832607L;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签颜色
     */
    private String tagColor;
}
