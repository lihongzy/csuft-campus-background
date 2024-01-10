package com.lihong.csuftcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lihong.csuftcampus.model.domain.PostThumb;

/**
 * 针对表【post_thumb(帖子点赞表)】的数据库操作Service
 */
public interface PostThumbService extends IService<PostThumb> {

    long doThumb(Long userId, Long postId);

    Boolean isThumb(Long userId, Long postId);
}
