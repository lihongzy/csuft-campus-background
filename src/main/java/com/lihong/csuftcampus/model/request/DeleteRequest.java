package com.lihong.csuftcampus.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 7850730750098916183L;

    /**
     * id
     */
    private Long id;

}
