package com.lihong.csuftcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lihong.csuftcampus.model.domain.Post;
import com.lihong.csuftcampus.model.request.PostAddRequest;

import java.util.List;

/**
 * 针对表【post(帖子表)】的数据库操作Service
 */
public interface PostService extends IService<Post> {
    Long addPost(PostAddRequest post);

    List<Post> listByUser();

    List<Post> searchPosts(Long userId);

    void validPost(Post post);

    boolean containsProfanity(String content);
}
