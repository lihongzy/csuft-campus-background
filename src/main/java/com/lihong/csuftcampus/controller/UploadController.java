package com.lihong.csuftcampus.controller;

import cn.hutool.core.util.StrUtil;
import com.lihong.csuftcampus.common.BaseResponse;
import com.lihong.csuftcampus.common.ErrorCode;
import com.lihong.csuftcampus.common.ResultUtil;
import com.lihong.csuftcampus.exception.BusinessException;
import com.lihong.csuftcampus.utils.MinioUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传模块
 */
@RequestMapping("/upload")
@RestController
@Slf4j
public class UploadController {
    @Autowired
    private MinioUtils minioUtils;

    /**
     * 上传图片
     *
     * @param image 图片文件
     * @return 上传图片存储的url地址
     */
    @PostMapping("/avatar")
    public BaseResponse<String> upload(MultipartFile image) {
        log.info("文件上传,文件名{}", image.getOriginalFilename());
        String url = minioUtils.upload(image);
        log.info("文件上传成功,文件访问URL:{}", url);
        if (StrUtil.hasBlank(url)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtil.success(url);
    }
}
