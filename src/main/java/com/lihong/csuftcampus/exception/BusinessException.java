package com.lihong.csuftcampus.exception;

import com.lihong.csuftcampus.common.ErrorCode;
import lombok.Getter;

/**
 *异常类
 */
@Getter
public class BusinessException extends RuntimeException {
    /**
     * 状态码
     */
    private final int code;
    /**
     * 状态码描述（详情）
     */
    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

}

