package com.lihong.csuftcampus.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户删除封装类
 */
@Data
public class UserDeleteRequest implements Serializable {
    private static final long serialVersionUID = 8184842984693162160L;

    /**
     * 用户ID
     */
    private Long id;

}
