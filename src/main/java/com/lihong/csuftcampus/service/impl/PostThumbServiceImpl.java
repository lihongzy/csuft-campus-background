package com.lihong.csuftcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihong.csuftcampus.common.ErrorCode;
import com.lihong.csuftcampus.exception.BusinessException;
import com.lihong.csuftcampus.mapper.PostThumbMapper;
import com.lihong.csuftcampus.model.domain.Post;
import com.lihong.csuftcampus.model.domain.PostThumb;
import com.lihong.csuftcampus.service.PostService;
import com.lihong.csuftcampus.service.PostThumbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PostThumbServiceImpl extends ServiceImpl<PostThumbMapper, PostThumb>
        implements PostThumbService {

    @Autowired
    private PostService postService;

    @Transactional
    @Override
    public long doThumb(Long userId, Long postId) {
        //判断帖子是否存在
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NO_FOUND_ERROR);
        }
        //判断是否已点赞
        //已点赞
        if (isThumb(userId, postId)) {
            LambdaUpdateWrapper<PostThumb> postThumbUpdateWrapper = new LambdaUpdateWrapper<>();
            postThumbUpdateWrapper
                    .eq(PostThumb::getUserId, userId)
                    .eq(PostThumb::getPostId, postId);
            boolean result = this.remove(postThumbUpdateWrapper);
            if (result) {
                //帖子点赞数量-1
                LambdaUpdateWrapper<Post> postLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                postLambdaUpdateWrapper
                        .eq(Post::getId, postId)
                        .setSql("thumbNum = thumbNum - 1");
                postService.update(postLambdaUpdateWrapper);
                return -1;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            PostThumb postThumb = new PostThumb();
            postThumb.setUserId(userId);
            postThumb.setPostId(postId);
            //未点赞
            boolean result = save(postThumb);
            if (result) {
                //帖子点赞数量+1
                LambdaUpdateWrapper<Post> postLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                postLambdaUpdateWrapper
                        .eq(Post::getId, postId)
                        .setSql("thumbNum = thumbNum + 1");
                postService.update(postLambdaUpdateWrapper);
                return 1;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }

    }

    @Override
    public Boolean isThumb(Long userId, Long postId) {
        LambdaQueryWrapper<PostThumb> postThumbLambdaQueryWrapper = new LambdaQueryWrapper<>();
        postThumbLambdaQueryWrapper
                .eq(PostThumb::getPostId, postId)
                .eq(PostThumb::getUserId, userId);
        long count = this.count(postThumbLambdaQueryWrapper);
        return count > 0;

    }

}



