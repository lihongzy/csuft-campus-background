package com.lihong.csuftcampus.controller;

import com.lihong.csuftcampus.common.BaseResponse;
import com.lihong.csuftcampus.common.ErrorCode;
import com.lihong.csuftcampus.common.ResultUtil;
import com.lihong.csuftcampus.exception.BusinessException;
import com.lihong.csuftcampus.model.dto.UserDTO;
import com.lihong.csuftcampus.model.request.AddCommentRequest;
import com.lihong.csuftcampus.model.request.CommentThumbsRequest;
import com.lihong.csuftcampus.model.vo.PostCommentsVO;
import com.lihong.csuftcampus.service.PostCommentsService;
import com.lihong.csuftcampus.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 帖子评论模块
 */
@RequestMapping("/comments")
@RestController
@Slf4j
public class PostCommentsController {
    @Autowired
    private PostCommentsService postCommentsService;

    /**
     * 发布评论
     *
     * @param addCommentRequest 发布评论封装类
     * @return String
     */
    @PostMapping("/add")
    public BaseResponse<String> addComment(@RequestBody AddCommentRequest addCommentRequest) {
        postCommentsService.addComment(addCommentRequest);
        return ResultUtil.success("添加成功");
    }

    /**
     * 获取帖子评论列表
     *
     * @param postId 帖子ID
     * @return commentsList
     */
    @GetMapping
    public BaseResponse<List<PostCommentsVO>> getListComments(Long postId) {
        if (postId == null || postId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<PostCommentsVO> commentsList = postCommentsService.getListComments(postId);
        if (commentsList == null) {
            throw new BusinessException(ErrorCode.NO_FOUND_ERROR);
        }
        return ResultUtil.success(commentsList);
    }

    /**
     * 评论点赞
     *
     * @param commentThumbsRequest 评论点赞封装类
     * @return 1成功点赞 -1取消点赞
     */
    @PostMapping("/thumb")
    public BaseResponse<Integer> thumbComment(@RequestBody CommentThumbsRequest commentThumbsRequest) {
        // 1. 判断请求非空
        if (commentThumbsRequest == null || commentThumbsRequest.getCommentId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 从线程中获取当前用户
        UserDTO user = UserHolder.getUser();

        Long userId = user.getId();
        Long commentId = commentThumbsRequest.getCommentId();

        int result = postCommentsService.thumbComment(userId, commentId);

        return ResultUtil.success(result);

    }

}
